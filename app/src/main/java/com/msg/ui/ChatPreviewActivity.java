package com.msg.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feihong.msg.sms.R;
import com.msg.bean.ShowMsg;
import com.msg.common.Configs;
import com.msg.utils.AudioRecord;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.widget.ChatTextPopDialog;

public class ChatPreviewActivity extends Activity implements OnClickListener {
	private ShowMsg mMsg;
	private ImageView mImg, mIvPlay;
	private Bitmap mBitmap;
	private ImageBitmapCache ibc;
	private RelativeLayout mLayoutRecord;
	private TextView mTvTitle;
	private AudioRecord mRecord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_chat_preview);
		mRecord = new AudioRecord();
		mLayoutRecord = (RelativeLayout) findViewById(R.id.layout_record);
		mLayoutRecord.setOnClickListener(this);
		mIvPlay = (ImageView) findViewById(R.id.iv_play);
		mIvPlay.setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mTvTitle = (TextView) findViewById(R.id.tv_content_title);
		ibc = ImageBitmapCache.getInstance();
		mImg = (ImageView) findViewById(R.id.iv_img);
		mImg.setOnClickListener(this);
		Bundle data = getIntent().getExtras();
		if (null != data) {
			mMsg = (ShowMsg) data.get("msg");
			String name = data.getString("name");
			if (!TextUtils.isEmpty(name)) {
				((TextView) findViewById(R.id.tv_title)).setText(name);
			} else {
				((TextView) findViewById(R.id.tv_title)).setText("短信预览");
			}
			if (mMsg != null) {
				String title = mMsg.getTitle();
				String msg = mMsg.getMsg();
				String url = mMsg.getVoiceUrl();
				String path = mMsg.getVoicePath();
				if (!TextUtils.isEmpty(url) || !TextUtils.isEmpty(path)) {
					mIvPlay.setVisibility(View.VISIBLE);
				}
				if (!TextUtils.isEmpty(title)) {
					mTvTitle.setText(title);
				} else {
					mLayoutRecord.setVisibility(View.GONE);
				}
				TextView et_content = (TextView) findViewById(R.id.tv_card_content);
				et_content.setText(msg);

				String url_img = mMsg.getImageUrl();
				String path_img = mMsg.getImagePath();
				if (!TextUtils.isEmpty(url_img)) {
					handlerImage(url_img, mImg);
				} else if (!TextUtils.isEmpty(path_img)) {
					mBitmap = AuxiliaryUtils.getDiskBitmap(path_img);
//					Uri uri = Uri.parse((String) path_img);
//					mBitmap = PhotoUtils.decodeUriAsBitmap(this,uri);
					mImg.setImageBitmap(mBitmap);
//					Picasso.with(this).load(new File(path)).into(mImg);
				} else {
					mImg.setVisibility(View.GONE);
				}
			}
		}
	}


	private void handlerImage(String url, ImageView icon) {
		if (!TextUtils.isEmpty(url)) {
			url = Configs.IMAGE_URL_DOMAIN + url;
			mBitmap = ibc.getBitmap(url);
			if (mBitmap == null) {
				ImageLoader il = new ImageLoader(icon, this,
						Configs.REFRESH_IMAGE);
				il.execute(url);
			} else {
				icon.setImageBitmap(mBitmap);
			}
		}
	}

	private boolean isPalying = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.iv_play:
			if (isPalying) {
				isPalying = false;
				time_count = 0;
				audio_length = 0;
				mIvPlay.setImageResource(R.drawable.movie_play01);
				mHanlder.removeMessages(3);
				mRecord.stopPlaying();
			} else {
				isPalying = true;
				mIvPlay.setImageResource(R.drawable.movie_play01_tap);
				playAudio();
			}
			break;
		case R.id.iv_img:
			ImageView view = new ImageView(this);
			String url = mMsg.getImageUrl();
			if (!TextUtils.isEmpty(url)) {
				mBitmap = ibc.getBitmap(Configs.IMAGE_URL_DOMAIN + url);
				view.setImageBitmap(mBitmap);
				if(null != mBitmap) {
					showImageInDialog(view,mBitmap);
				}
			} else if(mBitmap != null) {
				showImageInDialog(view,mBitmap);
			}
			break;
		}
	}

	/**
	 * 查看大图
	 * 
	 * @param view
	 */
	private void showImageInDialog(View view,Bitmap mBitmap) {
		 ChatTextPopDialog.Builder chatbuilder=new ChatTextPopDialog.Builder(this);
         chatbuilder.setContentView(mBitmap);
         chatbuilder.create().show();
	}

	public void playAudio() {
		String url = mMsg.getVoiceUrl();
		String path = mMsg.getVoicePath();
		if (!TextUtils.isEmpty(path)) {
			audio_length = mRecord.getLength(path);
			mRecord.startPlaying(path);
			mHanlder.sendEmptyMessageDelayed(3, 1000);
		} else {
			AuxiliaryUtils.downLoadAudio(Configs.AUDIO_URL_DOMAIN + url,
					mHanlder);
		}
	}

	int audio_length;
	int time_count = 0;
	@SuppressLint("HandlerLeak")
	private Handler mHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
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
				Toast.makeText(ChatPreviewActivity.this, "无内容",
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecord.stopPlaying();
	}
}
