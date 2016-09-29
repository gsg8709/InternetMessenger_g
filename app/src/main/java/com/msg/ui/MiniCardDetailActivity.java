package com.msg.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feihong.msg.sms.R;
import com.msg.bean.Contacts;
import com.msg.bean.MiniCardListResult.MiniCard;
import com.msg.bean.ShowMsg;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AudioRecord;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.HtmlRegexpUtil;
import com.msg.utils.IOUtil;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.utils.SendMessage;
import com.msg.utils.UserManager;
import com.msg.utils.Utils;
import com.msg.widget.ProgressWebView;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

@SuppressLint("HandlerLeak")
public class MiniCardDetailActivity extends Activity implements
		OnClickListener, HttpHandlerListener {
	private MiniCard mCard;
	private TextView mTvTitle;
	private ImageBitmapCache ibc;
	private Bitmap mBitmap;
	private ImageView mImg, mIvPlay;
	private AudioRecord mRecord;
	private ArrayList<Contacts> mChooseContacts = new ArrayList<Contacts>();
	private StringBuilder mContacts = new StringBuilder();
	private TextView mEtContacts;
	private IMStorageDataBase db;
	private RelativeLayout mLayoutRecord;
	private ProgressDialog mDialog;
	private boolean isVipUser = false;//是否为包月用户，默认不是
	private String msncontent = "";
	private String toUid = "";
	private int miniPic = 0;
	private ProgressWebView webview;
    private boolean ismoreMinPic = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mini_card_detail);

		mRecord = new AudioRecord();
		db = new IMStorageDataBase(this);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("发送中，请稍后");
		Bundle data = getIntent().getExtras();
		
	
		if (null != data) {
			mCard = (MiniCard) data.getSerializable("card");
			if(HtmlRegexpUtil.hasSpecialChars(mCard.getMSG())) {
				ismoreMinPic = true;
			}
			miniPic = mCard.getNID();
			String title = mCard.getTITLE();
			mTvTitle = ((TextView) findViewById(R.id.tv_card_name));
			((TextView) findViewById(R.id.tv_title)).setText("迷你卡片");
			mLayoutRecord = (RelativeLayout) findViewById(R.id.layout_record);
			webview = (ProgressWebView) findViewById(R.id.webview);
//			((TextView) findViewById(R.id.tv_card_content)).setText(Html.fromHtml(mCard.getMSG(),imageGetter, null));
			mIvPlay = (ImageView) findViewById(R.id.iv_play);
			mIvPlay.setOnClickListener(this);
			
			findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_return).setOnClickListener(this);
			findViewById(R.id.btn_title_send).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_title_send).setOnClickListener(this);
			mEtContacts = (TextView) findViewById(R.id.et_choose_contacts);
			mEtContacts.setOnClickListener(this);
			ibc = ImageBitmapCache.getInstance();
			mImg = (ImageView) findViewById(R.id.iv_img);
			mImg.setOnClickListener(this);
			String image = mCard.getIMAGE();
			String audio = mCard.getAUDIO();
			if (!TextUtils.isEmpty(audio)) {
				mIvPlay.setVisibility(View.VISIBLE);
			}

			if (!TextUtils.isEmpty(title)) {
				mTvTitle.setText(title);
			} else if (TextUtils.isEmpty(audio)) {
				mLayoutRecord.setVisibility(View.GONE);
			}

			handlerImg(image);
		}
		
		showWebPage();
	}
	
	private void showWebPage() {
		 webview.getSettings().setJavaScriptEnabled(true);
		 webview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	            @Override
	            public void onFocusChange(View v, boolean hasFocus) {
	                if (hasFocus) {
	                    try {
	                        // 禁止网页上的缩放
	                        Field defaultScale = WebView.class
	                                .getDeclaredField("mDefaultScale");
	                        defaultScale.setAccessible(true);
	                        defaultScale.setFloat(webview, 1.0f);
	                    } catch (SecurityException e) {
	                        e.printStackTrace();
	                    } catch (IllegalArgumentException e) {
	                        e.printStackTrace();
	                    } catch (IllegalAccessException e) {
	                        e.printStackTrace();
	                    } catch (NoSuchFieldException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
		 });
	     webview.setDownloadListener(new DownloadListener() {
	          @Override
	           public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
	               if (url != null && url.startsWith("http://"))
	                   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	           }
	       });

	     if(mCard != null) {
	    	 webview.loadUrl(Configs.MINICARD_URL + mCard.getNID());
	     }
					
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecord.stopPlaying();
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

	private void sendMsg() {
		String uid = UserManager.getUserinfo(this).getUID();
		String title = mCard.getTITLE();
		String msg = Html.fromHtml(mCard.getMSG()).toString();
		
		long send_time = System.currentTimeMillis();
		String image_url = mCard.getIMAGE();
		String voice_url = mCard.getAUDIO();
		int nid = mCard.getNID();
		ShowMsg sm = null;

		if (mChooseContacts.size() == 0) {
			AuxiliaryUtils.toast(this, "请选择联系人");
			return;
		}

		for (int i = 0; i < mChooseContacts.size(); i++) {
			Contacts c = mChooseContacts.get(i);
			sm = new ShowMsg(c.getTEL(), c.getNAME(), uid,
					Configs.MSG_TYPE_INTER_SMS, Configs.SEND_MSG,
					Configs.MSG_READED, image_url, "", voice_url, "", title,
					msg, send_time + "",nid + "*" + "0");
			db.saveMsg(sm);
		}
		/**
		 * 如果是包月用户，调用短信发送
		 */
		if(isVipUser) {
			sendMessageByClient(toUid,title);
		} else {//调用服务器端发送
			sendMsgByInternet(sm);
		}
		
	}

	private void sendMsgByInternet(ShowMsg msg) {
		mDialog.show();
		String message = msg.getMsg();
		String title = msg.getTitle();
		String sendUid = msg.getToJid();
		String toUid = mContacts.substring(0, mContacts.length() - 1);
		String image_url = msg.getImageUrl();
		String audio_url = msg.getVoiceUrl();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("UID", toUid));
		if(ismoreMinPic) {
			 params.add(new BasicNameValuePair("MSG", Configs.MINICARD_URL  + miniPic));
		} else {
			params.add(new BasicNameValuePair("MSG", message.trim()));
		}
		params.add(new BasicNameValuePair("TITLE", title));
		params.add(new BasicNameValuePair("SENDUID", sendUid));
		params.add(new BasicNameValuePair("IMAGE", image_url));
		params.add(new BasicNameValuePair("AUDIO", audio_url));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.MSG_ADD_ADDRESS, HttpRequestType.POST, params,
				true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	private boolean isPalying = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_send:
			/**
			 * 验证用户是否包月
			 */
			new CheckVIPTask(toUid).execute();
		
			break;
		case R.id.btn_return:
			this.finish();
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
		case R.id.iv_img:
			ImageView view = new ImageView(this);
			if (mCard != null) {
				mBitmap = ibc.getBitmap(Configs.IMAGE_URL_DOMAIN
						+ mCard.getIMAGE());
				if (null != mBitmap) {
					view.setImageBitmap(mBitmap);
//					showImageInDialog(view);
				}
			}
			break;
		case R.id.et_choose_contacts:
			goChooseContacts();
			break;
			
		}
	}

	/**
	 * 选择联系人
	 */
	private void goChooseContacts() {
		mContacts = new StringBuilder("");
		Intent intent = new Intent(this, ContactsChooseActivity.class);
		intent.putExtra("page", Configs.FROM_CARD_PAGE);
		startActivityForResult(intent, 0);
	}

	public void playAudio() {
		String path = Configs.VOICE_PATH
				+ AuxiliaryUtils.md5(IOUtil
						.getFilename(Configs.AUDIO_URL_DOMAIN
								+ mCard.getAUDIO()));
		File destFile = new File(path);
		if (!destFile.exists()) {
			mLayoutRecord.setEnabled(false);
			AuxiliaryUtils.downLoadAudio(
					Configs.AUDIO_URL_DOMAIN + mCard.getAUDIO(), mHanlder);
		} else {
			audio_length = mRecord.getLength(path);
			mRecord.startPlaying(path);
			mHanlder.sendEmptyMessageDelayed(3, 1000);
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
				Toast.makeText(MiniCardDetailActivity.this, "无内容",
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (null != data) {
			Bundle bundle = data.getExtras();
			mChooseContacts = (ArrayList<Contacts>) bundle
					.getSerializable("result");
			for (int i = 0; i < mChooseContacts.size(); i++) {
				Contacts c = mChooseContacts.get(i);
				mContacts.append(c.getTEL()).append(",");
			}
			toUid = mContacts.substring(0, mContacts.length() - 1);
			mEtContacts.setText(toUid);
			/**
			 * 验证用户是否包月
			 */
//			new CheckVIPTask(toUid).execute();
		}
	}
	
	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
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
					dissmissDialog();
					if (result.contains("\"state\":true")) {

						AuxiliaryUtils.toast(MiniCardDetailActivity.this,
								"发送成功");
						handleResult(result);
						MiniCardDetailActivity.this.finish();
					} else {
						AuxiliaryUtils.toast(MiniCardDetailActivity.this,
								"发送失败");
					}
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dissmissDialog();
					AuxiliaryUtils.startNetSetting(MiniCardDetailActivity.this);
				}
			});
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dissmissDialog();
					AuxiliaryUtils.toast(MiniCardDetailActivity.this,
							R.string.msg_network_error);
				}
			});
			break;
		}
	}
	
	/**
	 * 处理返回数据
	 * 
	 */
	private void handleResult(String content) {
		if (!TextUtils.isEmpty(content)) { 
			try {
				JSONObject jsonObject = new JSONObject(content);
			    //调用发送短信程序
				msncontent = jsonObject.getString("msg");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
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
		private boolean resultCode = false;
		private String userid = "";
		
		public CheckVIPTask(String userid) {
			this.userid = userid;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String baseURL = Configs.USER_CHECKVIP;
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("UID",userid));
			responseResult = Utils.getData(baseURL, requestParams);

			if (!TextUtils.isEmpty(responseResult)) {
				try {
					JSONObject jsonObject = new JSONObject(responseResult);
					resultCode = jsonObject.getBoolean("state");
					/**
					 * state = false 不是包月用户
					 */
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
				isVipUser = true;
				/**
				 * 不是包月用户，调用客户端发短信
				 */
			}
			sendMsg();
		}
		
	}
	
	/**
	 * 客户端发送短信
	 */
	private void sendMessageByClient(String toUserid, String title) {
		SendMessage mSendMessage = new SendMessage(this);
		mSendMessage.send(toUserid, title + "。  点击读信" + Configs.MINICARD_SEND_URL + mCard.getNID() + "\n" + "【互联短信】");
		AuxiliaryUtils.toast(MiniCardDetailActivity.this,"发送成功");
				
	}
	
}
