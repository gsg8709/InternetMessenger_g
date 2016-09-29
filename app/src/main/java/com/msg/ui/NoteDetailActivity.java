package com.msg.ui;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.feihong.msg.sms.R;
import com.msg.bean.Contacts;
import com.msg.bean.NoteResult.Note;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AudioRecord;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.DialogUtil.DialogOnClickListener;
import com.msg.utils.IOUtil;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.utils.TimeRender;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 删除人脉记事
 * 
 * @author Chris
 * 
 */
public class NoteDetailActivity extends Activity implements OnClickListener,
		HttpHandlerListener {
	private TextView mTvContent, mTvContacts, mTvTime;
	private Note mNote;
	private ProgressDialog mDialog;
	private ImageView mImg, mIvPlay;
	private ImageBitmapCache ibc;
	private AudioRecord mRecord;
	private int mIndex = -1;
	private IMStorageDataBase mDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_note_detail);

		mDB = new IMStorageDataBase(this);
		mRecord = new AudioRecord();
		Bundle data = getIntent().getExtras();
		((TextView) findViewById(R.id.tv_title)).setText("人脉记事");
		mTvContacts = (TextView) findViewById(R.id.tv_contacts);
		mTvContent = (TextView) findViewById(R.id.et_content);
		mTvTime = (TextView) findViewById(R.id.tv_time);

		findViewById(R.id.btn_title_del).setOnClickListener(this);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_title_del).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		mIvPlay = (ImageView) findViewById(R.id.iv_play);
		mIvPlay.setOnClickListener(this);
		mImg = (ImageView) findViewById(R.id.iv_img);
		mImg.setOnClickListener(this);
		ibc = ImageBitmapCache.getInstance();
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("删除中，请稍后");

		if (data != null) {
			mNote = (Note) data.get("note");
			mIndex = data.getInt("index");
			mTvContent.setText(mNote.getWORD());
			mTvTime.setText(TimeRender.getDate(mNote.getEDITTIME()));
			String image = mNote.getIMAGE();
			String audio = mNote.getAUDIO();
			if (TextUtils.isEmpty(image)) {
				mImg.setVisibility(View.GONE);
			} else {
				handlerImg(image);
			}

			if (TextUtils.isEmpty(audio)) {
				mIvPlay.setVisibility(View.GONE);
			} else {
				mIvPlay.setVisibility(View.VISIBLE);
			}

			int cid = mNote.getCID();
			Contacts c = mDB.getFriendInfo(cid);
			if (c != null) {
				mTvContacts.setText(c.getNAME());
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecord.stopPlaying();
	}

	private Bitmap mBitmap;

	/**
	 * 加载新鲜事图片
	 * 
	 * @param url
	 */
	private void handlerImg(String url) {
		if (!TextUtils.isEmpty(url) && !url.equalsIgnoreCase("null")) {
			url = Configs.IMAGE_URL_DOMAIN + url;
			mBitmap = ibc.getBitmap(url);
			if (mBitmap == null) {
				ImageLoader il = new ImageLoader(mImg, this,
						Configs.REFRESH_IMAGE);
				il.execute(url);
			} else {
				mImg.setImageBitmap(mBitmap);
			}
		}
	}

	private void delNote() {
		String wid = mNote.getWID() + "";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("WID", wid));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.WORDPAD_DELETE_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_del:
			DialogUtil.dialogMessage(this, "", "是否确认删除", "确定", "", "取消",
					new DialogOnClickListener() {

						@Override
						public void onDialogClick(DialogInterface dialog,
								int whichButton, int source) {
							if (source == DialogUtil.SOURCE_POSITIVE) {
								mDialog.show();
								delNote();
							}
						}
					});
			break;
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.iv_img:
			ImageView view = new ImageView(this);
			if (mNote != null) {
				mBitmap = ibc.getBitmap(Configs.IMAGE_URL_DOMAIN
						+ mNote.getIMAGE());
				if (null != mBitmap) {
					view.setImageBitmap(mBitmap);
					showImageInDialog(view);
				}
			}
			break;
		case R.id.iv_play:
			if (isPalying) {
				isPalying = false;
				time_count = 0;
				audio_length = 0;
				mIvPlay.setImageResource(R.drawable.movie_play01);
				mRecord.stopPlaying();
				mHanlder.removeMessages(3);
			} else {
				isPalying = true;
				mIvPlay.setImageResource(R.drawable.movie_play01_tap);
				playAudio();
			}
			break;
		}
	}

	public void playAudio() {
		String path = Configs.VOICE_PATH
				+ AuxiliaryUtils.md5(IOUtil
						.getFilename(Configs.AUDIO_URL_DOMAIN
								+ mNote.getAUDIO()));
		File destFile = new File(path);
		if (!destFile.exists()) {
			AuxiliaryUtils.downLoadAudio(
					Configs.AUDIO_URL_DOMAIN + mNote.getAUDIO(), mHanlder);
		} else {
			audio_length = mRecord.getLength(path);
			mRecord.startPlaying(path);
			mHanlder.sendEmptyMessageDelayed(3, 1000);
		}
	}

	private boolean isPalying = false;
	int audio_length;
	int time_count = 0;
	private Handler mHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Configs.DOWNLOAD_AUDIO_SUCCESS: // 音频下载成功
				String path = (String) msg.obj;
				audio_length = mRecord.getLength(path);
				mRecord.startPlaying(path);
				mHanlder.sendEmptyMessageDelayed(3, 1000);
				break;
			case Configs.DOWNLOAD_AUDIO_FAIL: // 音频下载失败
				mIvPlay.setImageResource(R.drawable.movie_play01);
				Toast.makeText(NoteDetailActivity.this, "无内容",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				time_count++;
				if (time_count == audio_length || audio_length == 0) {
					isPalying = false;
					mIvPlay.setImageResource(R.drawable.movie_play01);
					time_count = 0;
				} else {
					mHanlder.sendEmptyMessageDelayed(3, 1000);
				}
				break;
			}
		}
	};

	/**
	 * 查看大图
	 * 
	 * @param view
	 */
	private void showImageInDialog(View view) {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		final AlertDialog dlg = ad.create();
		dlg.setCanceledOnTouchOutside(true);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		dlg.onWindowAttributesChanged(lp);
		lp.x = 0;
		lp.y = 0;
		dlg.show();
		dlg.getWindow().setContentView(view);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.cancel();
			}
		});
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			byte[] data, int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			String result = new String(data);
			if (result.contains("\"state\":true")) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AuxiliaryUtils.toast(NoteDetailActivity.this, "删除成功");
						Intent intent = new Intent(NoteDetailActivity.this,
								NoteActivity.class);
						if (mIndex != -1) {
							intent.putExtra("index", mIndex);
						}
						setResult(RESULT_FIRST_USER, intent);
						NoteDetailActivity.this.finish();
					}
				});
			} else {
				AuxiliaryUtils.toast(NoteDetailActivity.this, "删除失败");
			}
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(NoteDetailActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(NoteDetailActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void dissmissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
}
