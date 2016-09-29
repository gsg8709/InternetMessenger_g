package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.bean.NoteResult.Note;
import com.msg.bean.UploadNoteResult;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AudioRecord;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.ContactUtils;
import com.msg.utils.HttpMultipartRequest;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.utils.PhotoUtils;
import com.msg.utils.TimeRender;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 新建人脉记事
 * 
 * @author Chris
 * 
 */
public class NoteAddActivity extends Activity implements OnClickListener,
		HttpHandlerListener, OnTouchListener {
	private TextView mTvDate;
	private EditText mTvContacts, mTvContent;
	private ArrayList<Contacts> mListContacts = new ArrayList<Contacts>();
	private ProgressDialog mDialog;
	private ImageView mImg;
	private PopupWindow mPopupWindow;
	private LinearLayout mLayoutRecord;
	private RelativeLayout mLayoutParent, mLayoutVoiceDialog;
	private Button mBtnTakePhoto, mBtnChoosePhoto, mBtnCancel;
	private TextView mTvRecord;
	private AudioRecord mRecord;

	/**
	 * 上传图片
	 */
	private static final String IMAGE_PATH = Configs.FILE_PATH + "img_temp.png";
	private static final String IMAGE_LOCATION = "file://" + IMAGE_PATH;
	private Uri imageUri;
	private static final int TAKE_PICTURE = 1;
	private static final int CROP_PICTURE = 2;
	private static final int CHOOSE_PICTURE = 3;
	private Bitmap mDetailBitmap;
	private boolean isNewVedio = false, isNewPhoto = false;
	private int mType;
	private Note mNote;
	private Bitmap mBitmap;
	private ImageBitmapCache ibc;
	private long mCid = -1;
	private IMStorageDataBase mDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_add_note);
		initView();

		mDb = new IMStorageDataBase(this);
		Bundle data = getIntent().getExtras();
		if (null != data) {
			mType = data.getInt("type");
			mNote = (Note) data.getSerializable("data");
			handlerInit(mType);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecord.stopPlaying();
	}

	private void handlerInit(int type) {
		switch (type) {
		case Configs.CREATE_NOTE:
			((TextView) findViewById(R.id.tv_title)).setText("新建记事");
			mImg.setImageResource(R.drawable.btn_choose_img);
			break;
		case Configs.EDIT_NOTE:
			((TextView) findViewById(R.id.tv_title)).setText("编辑记事");
			if (mNote != null) {
				String content = mNote.getWORD();
				mTvContent.setText(content);
				mTvContent.setSelection(content.length());
				mTvDate.setText(TimeRender.getDate(mNote.getEDITTIME()));
				String image = mNote.getIMAGE();
				if (TextUtils.isEmpty(image)) {
					mImg.setImageResource(R.drawable.btn_choose_img);
				} else {
					handlerImg(mNote.getIMAGE());
				}

				Contacts c = mDb.getFriendInfo(mNote.getCID());
				mTvContacts.setText(c.getNAME());
			}
			break;
		}
		if (type != Configs.QUERY_SMS_MODE) {
			mRecord.removeFile();
		}
	}

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
		ibc = ImageBitmapCache.getInstance();
		imageUri = Uri.parse(IMAGE_LOCATION);
		((TextView) findViewById(R.id.tv_title)).setText("新建记事");
		mTvDate = (TextView) findViewById(R.id.tv_time);
		mTvDate.setText(TimeRender.getDate());
		mTvContacts = (EditText) findViewById(R.id.tv_contacts);
		mTvContent = (EditText) findViewById(R.id.et_content);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_data).setOnClickListener(this);
		findViewById(R.id.btn_title_save).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_title_save).setOnClickListener(this);
		findViewById(R.id.btn_choose).setOnClickListener(this);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("保存中，请稍后");
		mLayoutVoiceDialog = (RelativeLayout) findViewById(R.id.layout_voice_dialog);
		mTvRecord = (TextView) findViewById(R.id.tv_record);
		mImg = (ImageView) findViewById(R.id.iv_img);
		mImg.setOnClickListener(this);
		mLayoutRecord = (LinearLayout) findViewById(R.id.layout_record);
		mLayoutRecord.setOnTouchListener(this);
		mLayoutRecord.setOnClickListener(null);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_save:
			if (mType == Configs.EDIT_NOTE) {
				updateNote();
			} else {
				uploadNote();
			}
			break;
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.btn_choose:
			Intent intent = new Intent(this, ContactsChooseActivity.class);
			intent.putExtra("page", Configs.FROM_NOTE_PAGE);
			startActivityForResult(intent, 0);
			break;
		case R.id.btn_data:
			showDateDialog();
			break;
		case R.id.iv_img:
			showPop();
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
		}
	}

	private void showDateDialog() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		new DatePickerDialog(this, datePickerDialog, year, monthOfYear,
				dayOfMonth).show();
	}

	DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String m = "";
			String d = "";
			if (monthOfYear < 10) {
				m = "0" + (monthOfYear + 1);
			} else {
				m = (monthOfYear + 1) + "";
			}

			if (dayOfMonth < 10) {
				d = "0" + dayOfMonth;
			} else {
				d = dayOfMonth + "";
			}
			mTvDate.setText(year + "-" + m + "-" + d);
		}
	};

	/**
	 * 接口BUG：人脉记事保存接口，cid {"state":false,"info":
	 * "  \n--- The error occurred in root/map/wordpad_SqlMap.xml.  \n--- The error occurred while applying a parameter map.  \n--- Check the insert_WORDPAD-InlineParameterMap.  \n--- Check the statement (update failed).  \n--- Cause: java.sql.SQLException: Data truncated for column 'CID' at row 1"
	 * }
	 * 
	 */
	private void uploadNote() {
		String uid = UserManager.getUserinfo(this).getUID();
		String word = mTvContent.getText().toString().trim();
		if (TextUtils.isEmpty(word)) {
			AuxiliaryUtils.toast(this, "请输入记事内容");
			return;
		}
		if (mListContacts.size() == 0) {
			AuxiliaryUtils.toast(this, "请选择联系人");
			return;
		}
		mDialog.show();
		String audio_path = Configs.FILE_PATH + "audio_temp.3gp";

		String date = mTvDate.getText().toString();
		long time = TimeRender.stringToLong(date, "yyyy-MM-dd");
		String dateline = time + "";
		dateline = dateline.substring(0, 10);
		List<String[]> stringParams = new ArrayList<String[]>();
		stringParams.add(new String[] { "UID", uid });
		stringParams.add(new String[] { "WORD", word });
		stringParams.add(new String[] { "EDITTIME", dateline });
		stringParams.add(new String[] { "CID", mCid + "" });
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
			params.add(new BasicNameValuePair("WORD", word));
			params.add(new BasicNameValuePair("EDITTIME", dateline));
			params.add(new BasicNameValuePair("CID", mListContacts.get(0)
					.getContactId() + ""));
			NetHttpHandler handler = new NetHttpHandler(this);
			handler.setHttpHandlerListener(this);
			handler.execute(Configs.WORDPAD_ADD_ADDRESS, HttpRequestType.POST,
					params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
		} else {
			HttpMultipartRequest.uploadFile(Configs.WORDPAD_ADD_ADDRESS,
					stringParams, fileParams, mHanlder);
		}
	}

	private void updateNote() {
		String uid = UserManager.getUserinfo(this).getUID();
		String word = mTvContent.getText().toString().trim();
		if (TextUtils.isEmpty(word)) {
			AuxiliaryUtils.toast(this, "请输入记事内容");
			return;
		}

		if (mListContacts.size() == 0) {
			mCid = mNote.getCID();
		}

		mDialog.show();
		String audio_path = Configs.FILE_PATH + "audio_temp.3gp";

		String date = mTvDate.getText().toString();
		long time = TimeRender.stringToLong(date, "yyyy-MM-dd");
		String dateline = time + "";
		dateline = dateline.substring(0, 10);
		List<String[]> stringParams = new ArrayList<String[]>();
		stringParams.add(new String[] { "UID", uid });
		stringParams.add(new String[] { "WORD", word });
		stringParams.add(new String[] { "WID", "" + mNote.getWID() });
		stringParams.add(new String[] { "EDITTIME", dateline });
		stringParams.add(new String[] { "CID", mCid + "" });
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
			params.add(new BasicNameValuePair("WORD", word));
			params.add(new BasicNameValuePair("EDITTIME", dateline));
			params.add(new BasicNameValuePair("WID", "" + mNote.getWID()));
			params.add(new BasicNameValuePair("CID", mCid + ""));
			NetHttpHandler handler = new NetHttpHandler(this);
			handler.setHttpHandlerListener(this);
			handler.execute(Configs.WORDPAD_UPDATE_ADDRESS,
					HttpRequestType.POST, params, true,
					NetHttpHandler.RECEIVE_DATA_MIME_STRING);
		} else {
			HttpMultipartRequest.uploadFile(Configs.WORDPAD_UPDATE_ADDRESS,
					stringParams, fileParams, mHanlder);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Configs.UPLOAD_FILE_SUCCESS:
				AuxiliaryUtils.toast(NoteAddActivity.this, "保存成功");
				dissmissDialog();
				Gson gson = new Gson();
				UploadNoteResult result = gson.fromJson(new String(
						(byte[]) msg.obj), UploadNoteResult.class);
				handlerUploadResult(result);
				break;
			case Configs.UPLOAD_FILE_FAIL:
				AuxiliaryUtils.toast(NoteAddActivity.this, "保存失败");
				dissmissDialog();
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_FIRST_USER:
			Bundle b = data.getExtras();
			mListContacts = (ArrayList<Contacts>) b.getSerializable("result");
			if (mListContacts.size() != 0) {
				mTvContacts.setText(mListContacts.get(0).getNAME());
				mCid = mListContacts.get(0).getContactId();
			}
			break;
		case RESULT_OK:
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
			break;
		}
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			byte[] data, int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			Gson gson = new Gson();
			UploadNoteResult result = gson.fromJson(new String(data),
					UploadNoteResult.class);
			handlerUploadResult(result);
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(NoteAddActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(NoteAddActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void handlerUploadResult(final UploadNoteResult result) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (result.isState()) {
					AuxiliaryUtils.toast(NoteAddActivity.this, "保存成功");
					Intent intent = new Intent(NoteAddActivity.this,
							NoteActivity.class);
					setResult(RESULT_OK, intent);
					NoteAddActivity.this.finish();
				} else {
					AuxiliaryUtils.toast(NoteAddActivity.this, "保存失败");
				}
			}
		});
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
}
