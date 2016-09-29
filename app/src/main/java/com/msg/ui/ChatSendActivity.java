package com.msg.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Html;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.StringCallback;
import com.msg.bean.Contacts;
import com.msg.bean.MiniCardListResult.MiniCard;
import com.msg.bean.NewsListResult.News;
import com.msg.bean.ShowMsg;
import com.msg.bean.SmsModeResult.SmsMode;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AudioRecord;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.HttpMultipartRequest;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.utils.PhotoUtils;
import com.msg.utils.SendMessage;
import com.msg.utils.TimeRender;
import com.msg.utils.UserManager;
import com.msg.utils.Utils;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 发送聊天信息
 * @author gengsong
 * @time 2014-3-28
 */
public class ChatSendActivity extends Activity implements OnClickListener,
		OnTouchListener, HttpHandlerListener {
	private LinearLayout mLayoutInputInterSms, mLayoutPreview;
	private RelativeLayout mLayoutInputSms;
	private int mType;
	private PopupWindow mPopupWindow;
	private RelativeLayout mLayoutParent, mLayoutVoiceDialog;
	private Button mBtnTakePhoto, mBtnChoosePhoto, mBtnCancel, mBtnMiniCard,
			mBtnNews, mBtnRecord;
	private static String IMAGE_PATH = Configs.FILE_PATH + "img_temp.png";
	private static final String IMAGE_LOCATION = "file://" + IMAGE_PATH;
	private Bitmap mDetailBitmap;
	private ImageBitmapCache ibc;
	private Uri imageUri;
	private ImageView mImg, mIvPreview;
	public static final int TAKE_PICTURE = 1;
	private static final int CROP_PICTURE = 2;
	private static final int CHOOSE_PICTURE = 3;
	private TextView mTvContent, mTvTime, mTvTitle;
	private EditText mEtContent, mEtTitle, mEtContentSms;

	private boolean isNewVedio = false, isNewPhoto = false;
	private AudioRecord mRecord;
	private int mChooseType;
	private String mImageUrl;
	private StringBuilder mContacts = new StringBuilder("");
	private ArrayList<Contacts> mTo;
	private IMStorageDataBase db;
	private boolean isSending = false;
	private ProgressDialog mDialog;
	private String toUserid;
	private CheckVIPTask mCheckVIPTask;
	private boolean resultCode = true;
	private boolean sendmessage = false; //是否可以发送信息到对方 默认否
	private String  miniContent;
    private int  miniPic;
    private boolean ismoreMinPic = false;
    private int SmsType; //1,右图有料 0,迷你卡片 2,内部短信 4.短信段子
    
	private static int MAX_TIME = 15;    //◊Ó≥§¬º÷∆ ±º‰£¨µ•Œª√Î£¨0Œ™Œﬁ ±º‰œﬁ÷∆
	private static int MIX_TIME = 1;     //◊Ó∂Ã¬º÷∆ ±º‰£¨µ•Œª√Î£¨0Œ™Œﬁ ±º‰œﬁ÷∆£¨Ω®“È…ËŒ™1
	
	private static int RECORD_NO = 0;  //≤ª‘⁄¬º“Ù
	private static int RECORD_ING = 1;   //’˝‘⁄¬º“Ù
	private static int RECODE_ED = 2;   //ÕÍ≥…¬º“Ù
	
	private static int RECODE_STATE = 0;      //¬º“Ùµƒ◊¥Ã¨
	
	private static float recodeTime=0.0f;    //¬º“Ùµƒ ±º‰
	private static double voiceValue=0.0; 
	private Dialog dialog;
	private Thread recordThread;
	private ImageView dialog_img;
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
	            getPhotoFileName());
	

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_chat_send);
		db = new IMStorageDataBase(this);
		initView();
		
		Bundle data = getIntent().getExtras();
		if (null != data) {
			mType = data.getInt("type");
			mTo = (ArrayList<Contacts>) data.get("jid");
			StringBuilder names = new StringBuilder("");
			if (mTo.size() > 1) {
				names.append("通知");
				((TextView) findViewById(R.id.tv_title)).setText(names
						.toString());
			} else {
				names.append(mTo.get(0).getNAME()).append(",");
				((TextView) findViewById(R.id.tv_title)).setText(names
						.substring(0, names.length() - 1));
			}
			toUserid = mTo.get(0).getTEL();
			switch (mType) {
			case Configs.SEND_FROM_INTER_SMS:
				mLayoutInputInterSms.setVisibility(View.VISIBLE);
				mLayoutInputSms.setVisibility(View.GONE);
				break;
			case Configs.SEND_FROM_SMS:
				mLayoutInputInterSms.setVisibility(View.GONE);
				mLayoutInputSms.setVisibility(View.VISIBLE);
				break;
			}
			mCheckVIPTask = new CheckVIPTask();
			mCheckVIPTask.execute();
			ShowMsg msg = (ShowMsg) data.get("msg");
		
			if (msg != null) {
			
				String title = "", content = "", image_path = "";
				content = msg.getMsg();
				title = msg.getTitle();
				image_path = msg.getImagePath();
				image_url = msg.getImageUrl();
				voice_url = msg.getVoiceUrl();
				isNewPhoto = false;
				isNewVedio = false;
				mEtTitle.setText(title);
				mEtContent.setText(content);
				if (!TextUtils.isEmpty(title)) {
					mEtTitle.setSelection(title.length());
				}
				mEtContent.setSelection(content.length());
				mEtContent.requestFocus();

				mEtContentSms.setText(content);
				mEtContentSms.setSelection(content.length());

				if (!TextUtils.isEmpty(image_path)) {
					mImg.setImageBitmap(BitmapFactory.decodeFile(image_path));
				} else {
					handlerImg(image_url);
				}
			}
		}
		
	}

	private void initView() {
		mRecord = new AudioRecord();
		ibc = ImageBitmapCache.getInstance();
		imageUri = Uri.parse(IMAGE_LOCATION);
		//imageUri = file:///sdcard/internetmsg/img_temp.png
		mLayoutInputInterSms = (LinearLayout) findViewById(R.id.layout_chat_input_inter_sms);
		mLayoutInputSms = (RelativeLayout) findViewById(R.id.layout_chat_input_sms);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_inter_sms_send).setOnClickListener(this);
		findViewById(R.id.btn_choose_img).setOnClickListener(this);
		findViewById(R.id.btn_preview).setOnClickListener(this);
		findViewById(R.id.btn_send).setOnClickListener(this);
		findViewById(R.id.btn_sms_mode).setOnClickListener(this);
		mIvPreview = (ImageView) findViewById(R.id.iv_preview);
		mIvPreview.setOnClickListener(this);
		mLayoutPreview = (LinearLayout) findViewById(R.id.layout_preview);
		mBtnRecord = (Button) findViewById(R.id.btn_record);
		mTvContent = (TextView) findViewById(R.id.tv_content);
		mTvTime = (TextView) findViewById(R.id.tv_time);
		mTvTitle = (TextView) findViewById(R.id.tv_content_title);
		mEtContent = (EditText) findViewById(R.id.et_content);
		mEtContentSms = (EditText) findViewById(R.id.et_content_sms);
		mEtTitle = (EditText) findViewById(R.id.et_title);
		mBtnRecord.setOnTouchListener(this);
		mLayoutParent = (RelativeLayout) this.findViewById(R.id.layout_parent);
		mLayoutVoiceDialog = (RelativeLayout) findViewById(R.id.layout_voice_dialog);
		mLayoutParent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hidePop();
				return false;
			}
		});
		mImg = (ImageView) findViewById(R.id.iv_img);
		mImg.setOnClickListener(this);
		LinearLayout layout_button = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.layout_chat_button_dialog, null);
		mBtnTakePhoto = (Button) layout_button
				.findViewById(R.id.settings_skin_camera);
		mBtnChoosePhoto = (Button) layout_button
				.findViewById(R.id.settings_skin_gallery);
		mBtnCancel = (Button) layout_button
				.findViewById(R.id.settings_skin_cancel);
		mBtnMiniCard = (Button) layout_button
				.findViewById(R.id.settings_skin_mini_card);
		mBtnNews = (Button) layout_button.findViewById(R.id.settings_skin_news);
		mBtnTakePhoto.setOnClickListener(this);
		mBtnChoosePhoto.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnMiniCard.setOnClickListener(this);
		mBtnNews.setOnClickListener(this);

		mPopupWindow = new PopupWindow(layout_button, -1, -2);
		mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style);

		mDialog = new ProgressDialog(this);
		mDialog.setMessage("发送中，请稍后");
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
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.btn_inter_sms_send:
			sendMsg();
			break;
		case R.id.btn_send:
			sendMsg();
			break;
		case R.id.btn_choose_img:
			showPop();
			break;
		case R.id.btn_preview:
			handlerPreview();
			break;
		case R.id.settings_skin_camera:
//			PhotoUtils.takeImage(this, imageUri, TAKE_PICTURE);
			Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        // 指定调用相机拍照后照片的储存路径
	        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
	        startActivityForResult(cameraintent, TAKE_PICTURE);
			
			hidePop();
			break;
		case R.id.settings_skin_gallery:
			PhotoUtils.chooseImage(this, imageUri, 480, 480, CHOOSE_PICTURE);
			hidePop();
			break;
		case R.id.settings_skin_cancel:
			hidePop();
			break;
		case R.id.settings_skin_mini_card:
			mChooseType = Configs.SEND_FOR_CHOOSE_MINI_CARD;
			Intent intent = new Intent(this, MiniCardActivity.class);
			intent.putExtra("type", Configs.SEND_FOR_CHOOSE_MINI_CARD);
			startActivityForResult(intent, 0);
			hidePop();
			break;
		case R.id.settings_skin_news:
			mChooseType = Configs.SEND_FOR_CHOOSE_NEWS;
			Intent intent_new = new Intent(this, NewsActivity.class);
			intent_new.putExtra("type", Configs.SEND_FOR_CHOOSE_NEWS);
			startActivityForResult(intent_new, 0);
			hidePop();
			break;
		case R.id.iv_img:
			showBigImage();
			break;
		case R.id.iv_preview:
			showBigImage();
			break;
		case R.id.btn_sms_mode:
			mChooseType = Configs.SEND_FOR_CHOOSE_SMS_MODE;
			Intent intent_sms_mode = new Intent(this, SmsModeActivity.class);
			intent_sms_mode.putExtra("type", Configs.SEND_FOR_CHOOSE_SMS_MODE);
			startActivityForResult(intent_sms_mode, 0);
			break;
		}
	}

	long send_time;
	String title, msg;
	String image_url = "", image_path = "", voice_url = "", voice_path = "";
	

	private void sendMsg() {
		String uid = UserManager.getUserinfo(this).getUID();

		switch (mType) {
		case Configs.SEND_FROM_INTER_SMS:
			title = mEtTitle.getText().toString().trim();
			msg = mEtContent.getText().toString().trim();

			if (TextUtils.isEmpty(title)) {
				AuxiliaryUtils.toast(this, "请输入短信正文的主题");
				return;
			}
			break;
		case Configs.SEND_FROM_SMS:
			msg = mEtContentSms.getText().toString().trim();
			title = msg;
			break;
        case Configs.SEND_FOR_CHOOSE_MINI_CARD:
            break;
		}
		send_time = System.currentTimeMillis();


		if (!TextUtils.isEmpty(msg)) {
			mDialog.show();

			if (isNewPhoto) {
				image_url = null;
				image_path = Configs.IMAGE_PATH + send_time + ".png";
				AuxiliaryUtils.storeFile(new File(IMAGE_PATH), image_path);
			}

			if (isNewVedio) {
				voice_url = null;
				voice_path = Configs.VOICE_PATH + send_time + ".amr";
				AuxiliaryUtils.storeFile(new File(mRecord.getmFileName()),
						voice_path);
			}

			ShowMsg sm = null;
			for (int i = 0; i < mTo.size(); i++) {
				Contacts c = mTo.get(i);
				switch (mType) {
				case Configs.SEND_FROM_INTER_SMS:
					sm = new ShowMsg(c.getTEL(), c.getNAME(), uid,
							Configs.MSG_TYPE_INTER_SMS, Configs.SEND_MSG,
							Configs.MSG_UNREADED, image_url, image_path,
							voice_url, voice_path, title, msg, send_time + "",miniPic + "*" +SmsType);
				    miniContent = sm.getMsg();
					break;
				case Configs.SEND_FROM_SMS:
					sm = new ShowMsg(c.getTEL(), c.getNAME(), uid,
							Configs.MSG_TYPE_SMS, Configs.SEND_MSG,
							Configs.MSG_READED, null, null, null, null, null,
							msg, send_time + "", null);
					break;
				}
 		    	db.saveMsg(sm);
				mContacts.append(c.getTEL()).append(",");
			}
            if(sendmessage && SmsType == 0 || SmsType == 4) {
            	if(SmsType != 4) {
            		if(SmsType != 0) {
            			sendMsgByInternet(sm);
            		} else {
	                     isCanSendMsg(toUserid,title + "。  点击读信"  +  "http://hulianxs.com/c/"  + miniPic+ "\n" + "【互联短信】");
	                     mDialog.dismiss();
	                     Intent intent_send_inter_sms = new Intent();
	                     intent_send_inter_sms.setClass(ChatSendActivity.this,
	         					ChatDetailActivity.class);
	         			setResult(RESULT_OK, intent_send_inter_sms);
            		}
         			finish();
            	} else {
            		if(!sendmessage) {
            			sendMsgByInternet(sm);
            		} else {
                        isCanSendMsg(toUserid,mEtContentSms.getText().toString());
                        mDialog.dismiss();
                        Intent intent_send_inter_sms = new Intent();
                        intent_send_inter_sms.setClass(ChatSendActivity.this,
            					ChatDetailActivity.class);
            			setResult(RESULT_OK, intent_send_inter_sms);
            			finish();
            		}
            	}
            } else {
                sendMsgByInternet(sm);
            }
		} else {
			AuxiliaryUtils.toast(this, "请输入短信正文");
		}
	}

	private void showBigImage() {
		ImageView view = new ImageView(this);
		mDetailBitmap = ibc.getBitmap(mImageUrl);
		if (mDetailBitmap != null) {
			view.setImageBitmap(mDetailBitmap);
			showImageInDialog(view);
		}
	}

	private void handlerPreview() {
		String title = mEtTitle.getText().toString().trim();
		String content = mEtContent.getText().toString().trim();
		if (TextUtils.isEmpty(title)) {
			AuxiliaryUtils.toast(this, "请输入短信正文的主题");
			return;
		}

		if (TextUtils.isEmpty(content)) {
			AuxiliaryUtils.toast(this, "请输入短信正文");
			return;
		}
		mTvTitle.setText(title);
		mTvContent.setText(content);
		mTvTime.setText(TimeRender.getDate());
		Bitmap bm = ibc.getBitmap(mImageUrl);
		if (bm != null) {
			mIvPreview.setImageBitmap(bm);
		}
		mLayoutPreview.setVisibility(View.VISIBLE);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == Activity.RESULT_OK) {
			if(mCheckVIPTask ==null) {
				return;
			}
			
			mCheckVIPTask = new CheckVIPTask();
			mCheckVIPTask.execute();
			switch (requestCode) {
			case TAKE_PICTURE:
//				PhotoUtils.cropImageUri(this, imageUri, 480, 480, CROP_PICTURE);
				startPhotoZoom(imageUri);
				break;
			case CROP_PICTURE:
				if (imageUri != null) {
					mDetailBitmap = PhotoUtils.decodeUriAsBitmap(this, imageUri);
					mDetailBitmap = rotateBitMap(mDetailBitmap);//解决旋转问题
					isNewPhoto = true;
					mImg.setImageBitmap(mDetailBitmap);
					mIvPreview.setImageBitmap(mDetailBitmap);
					SmsType = 2;
				}
				break;
			case CHOOSE_PICTURE:
				
				if (imageUri != null) {
					mDetailBitmap = PhotoUtils
							.decodeUriAsBitmap(this, imageUri);
					isNewPhoto = true;
					ibc.addCacheBitmap(mDetailBitmap, mImageUrl);
					mImg.setImageBitmap(mDetailBitmap);
					mIvPreview.setImageBitmap(mDetailBitmap);
					SmsType = 2;
				}
				
				if (data != null)
	                // setPicToView(data);
	                sentPicToNext(data);
				break;
			}
		} else if (resultCode == Activity.RESULT_FIRST_USER) {
			Bundle bundle = data.getExtras();
			if (null != bundle) {
				String title = "", content = "", image_path = "";
				switch (mChooseType) {
				case Configs.SEND_FOR_CHOOSE_SMS_MODE://短信段子
					SmsMode sm = (SmsMode) bundle.get("sm");
					mEtContentSms.setText(sm.getMSG());
					image_url = null;
					voice_url = null;
					isNewPhoto = false;
					isNewVedio = false;
					SmsType = 4;
					break;
				case Configs.SEND_FOR_CHOOSE_MINI_CARD://迷你卡片
					MiniCard card = (MiniCard) bundle.get("card");
					content = card.getMSG();
					title = card.getTITLE();
					image_path = card.getIMAGE();
					image_url = card.getIMAGE();
					voice_url = card.getAUDIO();
					mEtContent.setEnabled(false);
                    miniPic = card.getNID();
                    ismoreMinPic = true;
                    SmsType = 0;
					break;
				case Configs.SEND_FOR_CHOOSE_NEWS://右图有料
					News news = (News) bundle.get("news");
					content = news.getMSG();
					title = news.getTITLE();
					image_path = news.getIMAGE();
					image_url = news.getIMAGE();
					voice_url = news.getAUDIO();
					miniPic = news.getNID();
					SmsType = 1;
					break;
				}
				mEtTitle.setText(title);
//				mEtContent.setText(content);
				mEtContent.setText(Html.fromHtml(content));
				mEtTitle.setSelection(title.length());
//				mEtContent.setSelection(content.length());
				mEtContent.requestFocus();
				handlerImg(image_path);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 三星手机调用相机拍照出现横竖屏切换
	 * @param bp
	 * @return
	 */
	public static Bitmap rotateBitMap(Bitmap bp) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postRotate(90);
        Bitmap nowBp = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
                        bp.getHeight(), matrix, true);
        if (bp.isRecycled()) {
                bp.recycle();
        }
        return nowBp;
	}
	
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CHOOSE_PICTURE);
    }

	
	 // 将进行剪裁后的图片传递到下一个界面上
    private void sentPicToNext(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            if (photo==null) {
//            	mImg.setImageResource(R.drawable.ic_launcher);
            }else {
            	mImg.setImageBitmap(photo);
            	mIvPreview.setImageBitmap(photo);
            }
           
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] photodata = baos.toByteArray();
            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
	
	/**
	 * 加载新鲜事图片
	 * 
	 * @param url
	 */
	private void handlerImg(String url) {
		if (!TextUtils.isEmpty(url) && !url.equalsIgnoreCase("null")) {
			mImageUrl = Configs.IMAGE_URL_DOMAIN + url;
			mDetailBitmap = ibc.getBitmap(mImageUrl);
			if (mDetailBitmap == null) {
				ImageLoader il = new ImageLoader(mImg, this,
						Configs.REFRESH_IMAGE);
				il.execute(mImageUrl);
			} else {
				mImg.setImageBitmap(mDetailBitmap);
			}
			IMAGE_PATH = mImageUrl;
		}
	}

	private void sendMsgByInternet(ShowMsg msg) {
		String message =  msg.getMsg();
		int type = msg.getMsgType();
		String title = msg.getTitle();
		String sendUid = msg.getToJid();
		String toUid = mContacts.substring(0, mContacts.length() - 1);
		String image_url = msg.getImageUrl();
		String audio_url = msg.getVoiceUrl();
		String image_path = msg.getImagePath();
		String audio_path = msg.getVoicePath();
		
		List<String[]> stringParams = null ;
		List<String[]> fileParams = null;

		if (type == Configs.MSG_TYPE_SMS) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UID", toUid));
			params.add(new BasicNameValuePair("MSG", message));
			params.add(new BasicNameValuePair("TITLE", title));
			params.add(new BasicNameValuePair("SENDUID", sendUid));
			NetHttpHandler handler = new NetHttpHandler(this);
			handler.setHttpHandlerListener(this);
			handler.execute(Configs.MSG_ADD_ADDRESS, HttpRequestType.POST,
					params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
		} else {
			if (!TextUtils.isEmpty(image_path)
					|| !TextUtils.isEmpty(audio_path)) {
			    stringParams = new ArrayList<String[]>();
				stringParams.add(new String[] { "UID", toUid });
				stringParams.add(new String[] { "MSG", message });
				stringParams.add(new String[] { "TITLE", title });
				stringParams.add(new String[] { "SENDUID", sendUid });
				
				
			    fileParams = new ArrayList<String[]>();
				if (!TextUtils.isEmpty(image_path)) {
					fileParams.add(new String[] { "file", "img_temp.png",
							"application/png", image_path });
				} else {
					stringParams.add(new String[] { "IMAGE", image_url });
				}

				if (!TextUtils.isEmpty(audio_path)) {
					fileParams.add(new String[] { "file", "audio_temp.amr",
							"application/amr", audio_path });
				} else {
					stringParams.add(new String[] { "AUDIO", audio_url });
				}

				
				/**
				 * 老的方法，没有回调
				 */
//				HttpMultipartRequest.uploadFile(Configs.MSG_ADD_ADDRESS,
//						stringParams, fileParams, mHanlder);
				/**
				 * 研究的成果
				 */
				if( TextUtils.isEmpty(image_path)) {
					image_path = IMAGE_PATH;
				}
				File image_file = new File(image_path);
				File audio_file = new File(audio_path);
				if(mType == 3 || SmsType == 2 ||mType == 1030) {
					uploadBigFile(toUid,message,title,sendUid,image_file,audio_file);
					/**
					 * 老的方法，没有回调
					 */
					HttpMultipartRequest.uploadFile(Configs.MSG_ADD_ADDRESS,
							stringParams, fileParams, mHanlder);
				} else {
					
				}
			} else {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("UID", toUid));
				params.add(new BasicNameValuePair("TITLE", title));
				params.add(new BasicNameValuePair("SENDUID", sendUid));
				params.add(new BasicNameValuePair("IMAGE", image_url));

                if(ismoreMinPic) {
                    params.add(new BasicNameValuePair("IMAGE", mImageUrl));
                    if(SmsType ==0) {
                    	 params.add(new BasicNameValuePair("MSG", "http://hulianxs.com/client/minicard/id/"  + miniPic));
                    } else {
                    	 params.add(new BasicNameValuePair("MSG", "http://hulianxs.com/c/"  + miniPic));
                    }
                   
                    File image_file = new File(image_path);
                    File audio_file = new File(audio_path);
                    
                    if(SmsType == 2) 
                    uploadBigFile(toUid,message,title,sendUid,image_file,audio_file);

    				/**
    				 * 老的方法，没有回调
    				 */
    				HttpMultipartRequest.uploadFile(Configs.MSG_ADD_ADDRESS,
    						stringParams, fileParams, mHanlder);
    				
    				if(TextUtils.isEmpty(image_path)) {
                        params.add(new BasicNameValuePair("IMAGE", mImageUrl));
                    }
                    params.add(new BasicNameValuePair("AUDIO", audio_url));
                    NetHttpHandler handler = new NetHttpHandler(this);
                    handler.setHttpHandlerListener(this);
                    handler.execute(Configs.MSG_ADD_ADDRESS, HttpRequestType.POST,
                            params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
                } else {
                    if(TextUtils.isEmpty(image_path)) {
                        params.add(new BasicNameValuePair("IMAGE", mImageUrl));
                    }
                    params.add(new BasicNameValuePair("MSG", message));
                    params.add(new BasicNameValuePair("AUDIO", audio_url));
                    NetHttpHandler handler = new NetHttpHandler(this);
                    handler.setHttpHandlerListener(this);
                    handler.execute(Configs.MSG_ADD_ADDRESS, HttpRequestType.POST,
                            params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
                }

			}
		}
	}
	
	/**
	 * 上传大文件
	 */
	private void uploadBigFile(final String toUid,final String message,final String title,final String sendUid,final File image_path,final File audio_path ) {
		OkHttpUtils.post(Configs.MSG_ADD_ADDRESS) // 请求方式和请求url, get请求不需要拼接参数，支持get，post，put，delete，head，options请求
				.tag(this)               // 请求的 tag, 主要用于取消对应的请求
				.connTimeOut(10000)      // 设置当前请求的连接超时时间
				.readTimeOut(10000)      // 设置当前请求的读取超时时间
				.writeTimeOut(10000)     // 设置当前请求的写入超时时间
				.cacheKey("cacheKey")    // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
				.cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST) // 缓存模式，详细请看第四部分，缓存介绍
				.params("UID", toUid)        // 添加请求参数
				.params("MSG", message)        // 支持多请求参数同时添加
				.params("TITLE", title)        // 添加请求参数
				.params("SENDUID", sendUid)        // 支持多请求参数同时添加
				.params("file", image_path) // 可以添加文件上传
				.params("file", audio_path) // 支持多文件同时添加上传
//				.addUrlParams("key", List<String> values)  //这里支持一个key传多个参数
//				.addFileParams("key", List<File> files)    //这里支持一个key传多个文件
//				.addFileWrapperParams("key", List< HttpParams.FileWrapper> fileWrappers) //这里支持一个key传多个文件
//				.addCookie("aaa", "bbb")                // 这里可以传递自己想传的Cookie
				//这里给出的泛型为 RequestInfo，同时传递一个泛型的 class对象，即可自动将数据结果转成对象返回
				.execute(new StringCallback() {
					@Override
					public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
						//上传成功
						String result = s;
						if (result.contains("\"state\":true")) {
							handlerSendSuccess(result);
						} else {
							AuxiliaryUtils.toast(ChatSendActivity.this, "发送失败");
							mDialog.dismiss();
						}
					}
				});
	}


	
	public static File getFilePath(String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath + fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	public static void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {
		}
	}

	private Handler mHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Configs.UPLOAD_FILE_SUCCESS:
				handlerSendSuccess("");
				break;
			case Configs.UPLOAD_FILE_FAIL:
				AuxiliaryUtils.toast(ChatSendActivity.this, "发送失败");
				mDialog.dismiss();
				ChatSendActivity.this.finish();
				break;
			}
		}
	};
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			mLayoutVoiceDialog.setVisibility(View.VISIBLE);
//			mRecord.startRecording(mRecord.getmFileName());
			
			if (RECODE_STATE != RECORD_ING) {
				mRecord.startRecording(mRecord.getmFileName());
				RECODE_STATE=RECORD_ING;
				showVoiceDialog();
				mythread();
			}
			break;
		case MotionEvent.ACTION_UP:
//			mRecord.stopRecording();
//			mBtnRecord.setText(mRecord.getLength(mRecord.getmFileName()) + "s");
//			mLayoutVoiceDialog.setVisibility(View.GONE);
//			isNewVedio = true;
			
			if (RECODE_STATE == RECORD_ING) {
				RECODE_STATE=RECODE_ED;
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				mRecord.stopRecording();			
						
				if (recodeTime < MIX_TIME) {
					RECODE_STATE=RECORD_NO;
				}else{
					mBtnRecord.setText(mRecord.getLength(mRecord.getmFileName()) + "s");
				}						
			}
			isNewVedio = true;
			break;
		}
		return false;
	}
	
	void mythread(){
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}
	
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE==RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				}else{
				try {
					Thread.sleep(200);
					recodeTime += 0.2;
					if (RECODE_STATE == RECORD_ING) {
						voiceValue = mRecord.getAmplitude();
						imgHandle.sendEmptyMessage(1);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {
            
				switch (msg.what) {
				case 0:
					//¬º“Ù≥¨π˝15√Î◊‘∂ØÕ£÷π
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE=RECODE_ED;
						if (dialog.isShowing()) {
							dialog.dismiss();
						}
						mRecord.stopRecording();	
								if (recodeTime < 1.0) {
									mBtnRecord.setText(mRecord.getLength(mRecord.getmFileName()) + "s");
									RECODE_STATE=RECORD_NO;
								}else{
									mBtnRecord.setText(mRecord.getLength(mRecord.getmFileName()) + "s");
								}
					}
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}
				
			}
		};
	};
	
	void setDialogImage(){
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		}else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		}else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		}else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		}else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		}else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		}else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		}else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		}else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		}else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		}else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		}else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		}else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		}else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			final byte[] data, int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String result = new String(data);
                    if (result.contains("\"state\":true")) {
                        handlerSendSuccess(result);
                    } else {
                        mDialog.dismiss();
                        AuxiliaryUtils.toast(ChatSendActivity.this, "发送失败,包含敏感词语");
                    }
                    ChatSendActivity.this.finish();
                }
            });
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mDialog.dismiss();
                    AuxiliaryUtils.startNetSetting(ChatSendActivity.this);
                }
            });
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mDialog.dismiss();
					AuxiliaryUtils.toast(ChatSendActivity.this,
							R.string.msg_network_error);
				}
			});
			break;
		}
	}
	
	void showVoiceDialog(){
		dialog = new Dialog(ChatSendActivity.this,R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog_img=(ImageView)dialog.findViewById(R.id.dialog_img);
		dialog.show();
	}

	private void handlerSendSuccess(String content) {
		Intent intent_send_inter_sms = new Intent();
		if (mEtContent.getVisibility() == View.VISIBLE) {
			AuxiliaryUtils.hideKeyboard(ChatSendActivity.this, mEtContent);
		} else {
			AuxiliaryUtils.hideKeyboard(ChatSendActivity.this, mEtContentSms);
		}

		mDialog.dismiss();
//		db.saveMsg(sm);
		AuxiliaryUtils.toast(ChatSendActivity.this, "发送成功");
		if (mTo.size() > 1) {
			/*
			 * intent_send_inter_sms.setClass(ChatSendActivity.this,
			 * MainActivity.class); startActivity(intent_send_inter_sms)
			 */;
		} else {
			intent_send_inter_sms.setClass(ChatSendActivity.this,
					ChatDetailActivity.class);
			setResult(RESULT_OK, intent_send_inter_sms);
		}
		if (!TextUtils.isEmpty(content)) { 
			try {
				JSONObject jsonObject = new JSONObject(content);
				if (sendmessage) {//调用发送短信程序
					String msncontent = jsonObject.getString("msg");
					isCanSendMsg(toUserid,msncontent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		ChatSendActivity.this.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecord.stopPlaying();
		//根据 Tag 取消请求
		OkHttpUtils.getInstance().cancelTag(this);
	}
	
	
	/**
	 * 
	 * @ClassName: CheckVIPTask 
	 * @Description: 检查用户是否包月
	 * @author gengsong@zhongsou.com
	 * @date 2014年4月15日 下午2:36:24 
	 * @version 3.5.2
	 */
	private class CheckVIPTask extends AsyncTask<Void, Void, Void> {

		private String responseResult = "";
		
		@Override
		protected Void doInBackground(Void... params) {
			String baseURL = Configs.USER_CHECKVIP;
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("UID",toUserid));
			responseResult = Utils.getData(baseURL, requestParams);

			if (!TextUtils.isEmpty(responseResult)) {
				try {
					JSONObject jsonObject = new JSONObject(responseResult);
					resultCode = jsonObject.getBoolean("state");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					resultCode = true;
					e.printStackTrace();
				}
			} else {
				resultCode = true;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!resultCode) {
				sendmessage = true;
				mEtTitle.setBackgroundResource(R.drawable.edit_input_blue);
				mEtContentSms.setBackgroundResource(R.drawable.edit_input_blue);
			}
		}
		
	}

    private void sendInfoByMyself() {
        SendInfoTask mSendInfoTask = new SendInfoTask();
        mSendInfoTask.execute();
    }



    private class SendInfoTask extends  AsyncTask<Void, Void, Void> {
        private String responseResult = "";

        @Override
        protected Void doInBackground(Void... params) {
            String baseURL = Configs.USER_CHECKVIP;
            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("UID",toUserid));
            responseResult = Utils.getData(baseURL, requestParams);

            if (!TextUtils.isEmpty(responseResult)) {
                try {
                    JSONObject jsonObject = new JSONObject(responseResult);
                    resultCode = jsonObject.getBoolean("state");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    resultCode = true;
                    e.printStackTrace();
                }
            } else {
                resultCode = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(!resultCode) {
                sendmessage = true;
                mEtTitle.setBackgroundResource(R.drawable.edit_input_blue);
                mEtContentSms.setBackgroundResource(R.drawable.edit_input_blue);
            }
        }
    }
	
    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
    
    /**
     * 检查敏感关键字是否能发送短信
     * 2016.9.21
     */
    private void isCanSendMsg(String toUserid, String msgContent) {
    	CheckMsgCanSendTask mCheckMsgCanSendTask = new CheckMsgCanSendTask(toUserid,msgContent);
    	mCheckMsgCanSendTask.execute();
    }
    
    
    private class CheckMsgCanSendTask extends AsyncTask<Void, Void, Void> {
        private String responseResult = "";
        private String toUserid;
        private String msgContent;
        private boolean resultCode;
        
        public CheckMsgCanSendTask(String toUserid, String msgContent) {
        	this.toUserid = toUserid;
        	this.msgContent = msgContent;
        }

		@Override
        protected Void doInBackground(Void... params) {
            String baseURL = Configs.MSG_CHECKVKEYWORD;
            List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
            requestParams.add(new BasicNameValuePair("MSG",msgContent));
            responseResult = Utils.getData(baseURL, requestParams);

            if (!TextUtils.isEmpty(responseResult)) {
                try {
                    JSONObject jsonObject = new JSONObject(responseResult);
                    resultCode = jsonObject.getBoolean("state");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } 
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(resultCode) {
            	SendMessage mSendMessage = new SendMessage(ChatSendActivity.this);
        		mSendMessage.send(toUserid, msgContent);
        		AuxiliaryUtils.toast(ChatSendActivity.this, "发送成功");
            }else {
            	AuxiliaryUtils.toast(ChatSendActivity.this, "发送失败，包含敏感词汇");
            }
        }
    }

}
