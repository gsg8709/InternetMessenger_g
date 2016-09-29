package com.msg.ui;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.bean.SmsRuleResult.SmsRule;
import com.msg.bean.UploadSmsRuleResult;
import com.msg.common.Configs;
import com.msg.utils.AudioRecord;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.HttpMultipartRequest;
import com.msg.utils.IOUtil;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.utils.PhotoUtils;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

public class OfHookDetailActivity extends Activity implements OnClickListener,
		OnTouchListener, HttpHandlerListener {
	final static int TYPE_STATRT = 1;
	final static int TYPE_END = 2;
	private int mType;
	private TextView mTvStartTime, mTvEndTime, mTvRepeat;
	private String[] mRepeatRes = new String[] { "无限制", "同一用户每天发送一次",
			"同一用户每周发送一次", "同一用户每月发送一次" };
	private EditText mEtContent;
	private ProgressDialog mDialog;
	private SmsRule mSmsRule;
	private int mIndex;
	private int mRepeat;
	private ImageView mImg;
	private PopupWindow mPopupWindow;
	private LinearLayout mLayoutRecord;
	private RelativeLayout mLayoutParent, mLayoutVoiceDialog;
	private Button mBtnTakePhoto, mBtnChoosePhoto, mBtnCancel;
	private TextView mTvRecord;
	private AudioRecord mRecord;
	private ProgressBar mPRecord;
	private String[] mRes = new String[] { ".", "..", "...", "....", ".....",
			"......" };

	private static final String IMAGE_PATH = Configs.FILE_PATH + "img_temp.png";
	private static final String IMAGE_LOCATION = "file://" + IMAGE_PATH;
	private Uri imageUri;

	/**
	 * 上传图片
	 */
	private static final int TAKE_PICTURE = 1;
	private static final int CROP_PICTURE = 2;
	private static final int CHOOSE_PICTURE = 3;
	private Bitmap mDetailBitmap;
	private boolean isNewVedio = false, isNewPhoto = false;
	private ImageBitmapCache ibc;
	private CheckBox mCbEffect;
	private boolean isCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_add_hook_sms);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecord.stopPlaying();
	}

	private void initData() {
		Bundle data = getIntent().getExtras();
		if (data != null) {
			mIndex = data.getInt("index");
			mSmsRule = (SmsRule) data.get("result");
			if (mIndex == Configs.EDIT_OFHOOK_SMS_MODE) {
				((TextView) findViewById(R.id.tv_title)).setText("编辑挂机短信");
				findViewById(R.id.layout_effect).setOnClickListener(this);
				mTvStartTime.setText(mSmsRule.getSTARTTIME());
				mTvEndTime.setText(mSmsRule.getENDTIME());
				mEtContent.setText(mSmsRule.getMSG());
				mEtContent.setSelection(mSmsRule.getMSG().length());
				mRepeat = Integer.parseInt(mSmsRule.getCLASSID() + "");
				mTvRepeat.setText(mRepeatRes[mRepeat]);
				int effect = mSmsRule.getSELECTED();
				isCheck = effect == 1 ? true : false;
				mCbEffect.setChecked(isCheck);
				handlerImg(mSmsRule.getIMAGE());
			} else if (mIndex == Configs.QUERY_OFHOOK_SMS_MODE) {
				((TextView) findViewById(R.id.tv_title)).setText("查看挂机短信");
				mTvStartTime.setText(mSmsRule.getSTARTTIME());
				mTvEndTime.setText(mSmsRule.getENDTIME());
				mEtContent.setText(mSmsRule.getMSG());
				mEtContent.setSelection(mSmsRule.getMSG().length());
				mEtContent.setEnabled(false);
				mRepeat = Integer.parseInt(mSmsRule.getCLASSID() + "");
				mTvRepeat.setText(mRepeatRes[mRepeat]);
				mPRecord = (ProgressBar) findViewById(R.id.p_record);
				findViewById(R.id.btn_title_finish).setVisibility(View.GONE);
				findViewById(R.id.layout_starttime).setOnClickListener(null);
				findViewById(R.id.layout_endtime).setOnClickListener(null);
				findViewById(R.id.layout_repeat).setOnClickListener(null);
				mLayoutRecord.setOnTouchListener(null);
				mLayoutRecord.setOnClickListener(this);
				int effect = mSmsRule.getSELECTED();
				isCheck = effect == 1 ? true : false;
				mCbEffect.setChecked(isCheck);
				String audio = mSmsRule.getAUDIO();
				String image = mSmsRule.getIMAGE();
				if (TextUtils.isEmpty(audio)) {
					mLayoutRecord.setVisibility(View.GONE);
				} else {
					mTvRecord.setText("点击播放");
				}

				if (TextUtils.isEmpty(image)) {
					mImg.setVisibility(View.GONE);
				} else {
					handlerImg(mSmsRule.getIMAGE());
				}
			} else {
				//接口bug，新建挂机短信不会覆盖以前生效挂机短信
				((TextView) findViewById(R.id.tv_title)).setText("新建挂机短信");
				findViewById(R.id.layout_effect).setOnClickListener(this);
			}
		}
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

	private void initView() {
		mRecord = new AudioRecord();
		imageUri = Uri.parse(IMAGE_LOCATION);
		ibc = ImageBitmapCache.getInstance();
		((TextView) findViewById(R.id.tv_title)).setText("新建挂机短信");
		findViewById(R.id.btn_title_finish).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_title_finish).setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.layout_starttime).setOnClickListener(this);
		findViewById(R.id.layout_endtime).setOnClickListener(this);
		findViewById(R.id.layout_repeat).setOnClickListener(this);
		mTvStartTime = (TextView) findViewById(R.id.tv_starttime);
		mTvEndTime = (TextView) findViewById(R.id.tv_endtime);
		mTvRepeat = (TextView) findViewById(R.id.tv_repeat);
		mEtContent = (EditText) findViewById(R.id.et_content);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("保存中，请稍后");
		mLayoutVoiceDialog = (RelativeLayout) findViewById(R.id.layout_voice_dialog);
		mTvRecord = (TextView) findViewById(R.id.tv_record);
		mLayoutRecord = (LinearLayout) findViewById(R.id.layout_record);
		mLayoutRecord.setOnTouchListener(this);
		mLayoutRecord.setOnClickListener(null);
		mImg = (ImageView) findViewById(R.id.iv_img);
		mImg.setOnClickListener(this);
		LinearLayout layout_button = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.layout_button_dialog, null);
		mBtnTakePhoto = (Button) layout_button
				.findViewById(R.id.settings_skin_camera);
		mBtnChoosePhoto = (Button) layout_button
				.findViewById(R.id.settings_skin_gallery);
		mBtnCancel = (Button) layout_button
				.findViewById(R.id.settings_skin_cancel);
		mBtnTakePhoto.setOnClickListener(this);
		mBtnChoosePhoto.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mCbEffect = (CheckBox) findViewById(R.id.cb_effect);

		mPopupWindow = new PopupWindow(layout_button, -1, -2);
		mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style);
		mLayoutParent = (RelativeLayout) findViewById(R.id.layout_parent);
		mLayoutParent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hidePop();
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_finish:
			if (mIndex == Configs.EDIT_OFHOOK_SMS_MODE) {// 修改
				updateRule();
			} else {// 新增
				uploadRule();
			}
			break;
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.layout_starttime:
			mType = TYPE_STATRT;
			showDialog();
			break;
		case R.id.layout_endtime:
			mType = TYPE_END;
			showDialog();
			break;
		case R.id.layout_repeat:
			DialogUtil.dialogList(this, "重发条件", mRepeatRes, "", "", "",
					new DialogUtil.DialogOnClickListener() {

						@Override
						public void onDialogClick(DialogInterface dialog,
								int whichButton, int source) {
							mRepeat = whichButton;
							mTvRepeat.setText(mRepeatRes[whichButton]);
						}
					});
			break;
		case R.id.iv_img:
			if (mIndex == Configs.EDIT_OFHOOK_SMS_MODE
					|| mIndex == Configs.CREATE_OFHOOK_SMS_MODE) {
				showPop();
			} else {
				ImageView view = new ImageView(this);
				if (mSmsRule != null) {
					mBitmap = ibc.getBitmap(Configs.IMAGE_URL_DOMAIN
							+ mSmsRule.getIMAGE());
					if (null != mBitmap) {
						view.setImageBitmap(mBitmap);
						showImageInDialog(view);
					}
				}
			}
			break;
		case R.id.settings_skin_camera:
			PhotoUtils.takeImage(this, imageUri, TAKE_PICTURE);
			hidePop();
			break;
		case R.id.settings_skin_gallery:
			PhotoUtils.chooseImage(this, imageUri, 480, 480, CHOOSE_PICTURE);
			hidePop();
			break;
		case R.id.settings_skin_cancel:
			hidePop();
			break;

		case R.id.layout_record:
			if (mSmsRule != null) {
				File destFile = new File(Configs.VOICE_PATH
						+ AuxiliaryUtils.md5(IOUtil
								.getFilename(Configs.AUDIO_URL_DOMAIN
										+ mSmsRule.getAUDIO())));
				if (!destFile.exists()) {
					mTvRecord.setText("加载中");
					mPRecord.setVisibility(View.VISIBLE);
				}
				mLayoutRecord.setEnabled(false);
				AuxiliaryUtils.downLoadAudio(Configs.AUDIO_URL_DOMAIN
						+ mSmsRule.getAUDIO(), mHanlder);
			}
			break;
		case R.id.layout_effect:
			isCheck = isCheck ? false : true;
			mCbEffect.setChecked(isCheck);
			break;
		}
	}

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

	private void showPop() {
		if (mPopupWindow.isShowing()) {
			hidePop();
		} else {
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(150);
			mPopupWindow.showAtLocation(mLayoutParent, Gravity.BOTTOM, 0, 0);
		}
	}

	private void hidePop() {
		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(150);
		mPopupWindow.dismiss();
	}

	/**
	 * 接口BUG ： 更新接口不能带CLASSID，新增接口带CLASSID和不带都报错
	 */
	private void updateRule() {
		String msg = mEtContent.getText().toString().trim();
		String audio_path = Configs.FILE_PATH + "audio_temp.3gp";
		if (TextUtils.isEmpty(msg)) {
			Toast.makeText(this, "请输入挂机短信内容", 0).show();
			return;
		}

		String uid = UserManager.getUserinfo(this).getUID();
		String start = mTvStartTime.getText().toString().trim();
		String end = mTvEndTime.getText().toString().trim();
		mDialog.show();
		List<String[]> stringParams = new ArrayList<String[]>();
		stringParams.add(new String[] { "UID", uid });
		stringParams.add(new String[] { "OID", mSmsRule.getOID() });
		stringParams.add(new String[] { "MSG", msg });
		stringParams.add(new String[] { "STARTTIME", start });
		stringParams.add(new String[] { "CLASSID", mRepeat + "" });
		stringParams.add(new String[] { "ENDTIME", end });
		stringParams.add(new String[] { "SELECTED", "" + (isCheck ? 1 : 0) });
		List<String[]> fileParams = new ArrayList<String[]>();
		if (isNewPhoto) {
			fileParams.add(new String[] { "file", "img_temp.png",
					"application/png", IMAGE_PATH });
		}
		if (isNewVedio) {
			fileParams.add(new String[] { "file", "audio_temp.3gp",
					"application/3gp", audio_path });
		}

		if (!isNewPhoto && !isNewVedio) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UID", uid));
			params.add(new BasicNameValuePair("OID", mSmsRule.getOID()));
			params.add(new BasicNameValuePair("MSG", msg));
			params.add(new BasicNameValuePair("STARTTIME", start));
			params.add(new BasicNameValuePair("CLASSID", mRepeat + ""));
			params.add(new BasicNameValuePair("ENDTIME", end));
			params.add(new BasicNameValuePair("SELECTED", ""
					+ (isCheck ? 1 : 0)));
			NetHttpHandler handler = new NetHttpHandler(this);
			handler.setHttpHandlerListener(this);
			handler.execute(Configs.CUSTOM_ONHOOK_UPDATE_ADDRESS,
					HttpRequestType.POST, params, true,
					NetHttpHandler.RECEIVE_DATA_MIME_STRING);
		} else {
			HttpMultipartRequest.uploadFile(
					Configs.CUSTOM_ONHOOK_UPDATE_ADDRESS, stringParams,
					fileParams, mHanlder);
		}
	}

	private void uploadRule() {
		String msg = mEtContent.getText().toString().trim();
		String audio_path = Configs.FILE_PATH + "audio_temp.3gp";
		if (TextUtils.isEmpty(msg)) {
			Toast.makeText(this, "请输入挂机短信内容", 0).show();
			return;
		}

		String uid = UserManager.getUserinfo(this).getUID();
		String start = mTvStartTime.getText().toString().trim();
		String end = mTvEndTime.getText().toString().trim();
		mDialog.show();
		List<String[]> stringParams = new ArrayList<String[]>();
		stringParams.add(new String[] { "UID", uid });
		stringParams.add(new String[] { "MSG", msg });
		stringParams.add(new String[] { "STARTTIME", start });
		stringParams.add(new String[] { "CLASSID", mRepeat + "" });
		stringParams.add(new String[] { "ENDTIME", end });
		stringParams.add(new String[] { "SELECTED", "" + (isCheck ? 1 : 0) });
		List<String[]> fileParams = new ArrayList<String[]>();
		if (isNewPhoto) {
			fileParams.add(new String[] { "file", "img_temp.png",
					"application/png", IMAGE_PATH });
		}

		if (isNewVedio) {
			fileParams.add(new String[] { "file", "audio_temp.3gp",
					"application/3gp", audio_path });
		}

		if (!isNewPhoto && !isNewVedio) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UID", uid));
			params.add(new BasicNameValuePair("MSG", msg));
			params.add(new BasicNameValuePair("STARTTIME", start));
			params.add(new BasicNameValuePair("CLASSID", mRepeat + ""));
			params.add(new BasicNameValuePair("ENDTIME", end));
			params.add(new BasicNameValuePair("SELECTED", ""
					+ (isCheck ? 1 : 0)));
			NetHttpHandler handler = new NetHttpHandler(this);
			handler.setHttpHandlerListener(this);
			handler.execute(Configs.CUSTOM_ONHOOK_ADD_ADDRESS,
					HttpRequestType.POST, params, true,
					NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN);
		} else {
			HttpMultipartRequest.uploadFile(Configs.CUSTOM_ONHOOK_ADD_ADDRESS,
					stringParams, fileParams, mHanlder);
		}
	}

	int audio_length;
	int time_count = 0;
	private Handler mHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Configs.DOWNLOAD_AUDIO_SUCCESS: // 音频下载成功
				mPRecord.setVisibility(View.GONE);
				mTvRecord.setText("播放中" + mRes[0]);
				String path = (String) msg.obj;
				audio_length = mRecord.getLength(path);
				mRecord.startPlaying(path);
				mHanlder.sendEmptyMessageDelayed(3, 1000);
				break;
			case Configs.DOWNLOAD_AUDIO_FAIL: // 音频下载失败
				mPRecord.setVisibility(View.GONE);
				mTvRecord.setText("点击播放");
				mLayoutRecord.setEnabled(true);
				Toast.makeText(OfHookDetailActivity.this, "音频下载失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				time_count++;
				if (time_count == audio_length || audio_length == 0) {
					mTvRecord.setText("点击播放");
					mLayoutRecord.setEnabled(true);
					time_count = 0;
				} else {
					mTvRecord.setText("播放中" + mRes[time_count % 6]);
					mHanlder.sendEmptyMessageDelayed(3, 1000);
				}
				break;
			case Configs.UPLOAD_FILE_SUCCESS:
				AuxiliaryUtils.toast(OfHookDetailActivity.this, "保存成功");
				dissmissDialog();
				Intent intent = new Intent(OfHookDetailActivity.this,
						OfHookActivity.class);
				setResult(RESULT_FIRST_USER, intent);
				OfHookDetailActivity.this.finish();
				break;
			case Configs.UPLOAD_FILE_FAIL:
				AuxiliaryUtils.toast(OfHookDetailActivity.this, "保存失败");
				dissmissDialog();
				break;
			}
		}
	};

	private void showDialog() {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		new TimePickerDialog(this, timePickerDialog, hour, minute, true).show();
	}

	TimePickerDialog.OnTimeSetListener timePickerDialog = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {
			String h = "";
			String m = "";
			if (hour < 10) {
				h = "0" + hour;
			} else {
				h = hour + "";
			}

			if (minute < 10) {
				m = "0" + minute;
			} else {
				m = minute + "";
			}
			if (mType == TYPE_STATRT) {
				mTvStartTime.setText(h + ":" + m);
			} else {
				mTvEndTime.setText(h + ":" + m);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				PhotoUtils.cropImageUri(this, imageUri, 480, 480, CROP_PICTURE);
				break;
			case CROP_PICTURE:
				if (imageUri != null) {
					mDetailBitmap = PhotoUtils
							.decodeUriAsBitmap(this, imageUri);
					isNewPhoto = true;
					mImg.setImageBitmap(mDetailBitmap);
				}
				break;
			case CHOOSE_PICTURE:
				if (imageUri != null) {
					mDetailBitmap = PhotoUtils
							.decodeUriAsBitmap(this, imageUri);
					isNewPhoto = true;
					mImg.setImageBitmap(mDetailBitmap);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void dissmissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLayoutVoiceDialog.setVisibility(View.VISIBLE);
			mRecord.startRecording(mRecord.getmFileName());
			break;
		case MotionEvent.ACTION_UP:
			mRecord.stopRecording();
			mTvRecord.setText(mRecord.getLength(mRecord.getmFileName()) + "s");
			mLayoutVoiceDialog.setVisibility(View.GONE);
			isNewVedio = true;
			break;
		}
		return false;
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			final byte[] data, final int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			OfHookDetailActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Gson gson = new Gson();
					UploadSmsRuleResult result = gson.fromJson(
							new String(data), UploadSmsRuleResult.class);
					if (result.isState()) {
						AuxiliaryUtils.toast(OfHookDetailActivity.this, "保存成功");
						Intent intent = new Intent(OfHookDetailActivity.this,
								OfHookActivity.class);
						setResult(RESULT_FIRST_USER, intent);
						OfHookDetailActivity.this.finish();
					} else {
						AuxiliaryUtils.toast(OfHookDetailActivity.this, "保存失败");
					}
					dissmissDialog();
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			OfHookDetailActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(OfHookDetailActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			OfHookDetailActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(OfHookDetailActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}
}
