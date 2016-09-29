package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.bean.Contacts;
import com.msg.bean.ShowMsg;
import com.msg.bean.SmsModeResult.SmsMode;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.SendMessage;
import com.msg.utils.UserManager;
import com.msg.utils.Utils;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 新建短信模板
 * 
 * @author Chris
 * 
 */
public class SmsModeDetailActivity extends Activity implements OnClickListener,
		HttpHandlerListener {
	private SmsMode mSmsMode;
	private TextView mEtContent;
	private int mType;
	private ProgressDialog mDialog;
	private ArrayList<Contacts> mChooseContacts = new ArrayList<Contacts>();
	private StringBuilder mContacts = new StringBuilder();
	private IMStorageDataBase db;
	private CheckVIPTask mCheckVIPTask;
	private boolean resultCode = true;
	private boolean sendmessage = false; //是否可以发送信息到对方 默认否
	private String toUid = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sms_detail);
		db = new IMStorageDataBase(this);
		Bundle data = getIntent().getExtras();
		if (null != data) {
			mType = data.getInt("type");
			mSmsMode = (SmsMode) data.getSerializable("sm");
			handlerInit(mType);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void handlerInit(int type) {
		((TextView) findViewById(R.id.tv_title)).setText("短信段子");
		findViewById(R.id.btn_title_send).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_title_send).setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mEtContent = (TextView) findViewById(R.id.tv_card_content);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("发送中，请稍等");
		if (mSmsMode != null) {
			mEtContent.setText(mSmsMode.getMSG());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.btn_title_send:
			Intent intent = new Intent(this, ContactsChooseActivity.class);
			intent.putExtra("page", Configs.FROM_SMS_MODE_PAGE);
			startActivityForResult(intent, 0);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			Bundle bundle = data.getExtras();
			mChooseContacts = (ArrayList<Contacts>) bundle
					.getSerializable("result");
			for (int i = 0; i < mChooseContacts.size(); i++) {
				Contacts c = mChooseContacts.get(i);
				mContacts.append(c.getTEL()).append(",");
			}
			
			toUid = mContacts.substring(0, mContacts.length() - 1);
			mCheckVIPTask = new CheckVIPTask(toUid);
			mCheckVIPTask.execute();
			
		}
	}

	private void sendMsg() {
		String uid = UserManager.getUserinfo(this).getUID();
		String msg = mSmsMode.getMSG();
		String nid = mSmsMode.getMOID();
		long send_time = System.currentTimeMillis();
		ShowMsg sm = null;

		if (mChooseContacts.size() == 0) {
			AuxiliaryUtils.toast(this, "请选择联系人");
			return;
		}

		for (int i = 0; i < mChooseContacts.size(); i++) {
			Contacts c = mChooseContacts.get(i);
			sm = new ShowMsg(c.getTEL(), c.getNAME(), uid,
					Configs.MSG_TYPE_SMS, Configs.SEND_MSG, Configs.MSG_READED,
					"", "", "", "", "", msg, send_time + "",nid + "*");
			db.saveMsg(sm);
		}
	
		if(sendmessage) {
			//通过短信发送
			String msncontent = msg;
			SendMessage mSendMessage = new SendMessage(this);
			mSendMessage.send(toUid, msncontent);
		} else {
			handlerSend(sm);
		}
		
	}

	private void handlerSend(ShowMsg msg) {
		mDialog.show();
		String message = msg.getMsg();
		String sendUid = msg.getToJid();
		String toUid = mContacts.substring(0, mContacts.length() - 1);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("UID", toUid));
		params.add(new BasicNameValuePair("MSG", message));
		params.add(new BasicNameValuePair("SENDUID", sendUid));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.MSG_ADD_ADDRESS, HttpRequestType.POST, params,
				true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			final byte[] data, final int mime) {
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

						AuxiliaryUtils
								.toast(SmsModeDetailActivity.this, "发送成功");
						SmsModeDetailActivity.this.finish();
					} else {
						AuxiliaryUtils
								.toast(SmsModeDetailActivity.this, "发送失败");
					}
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			SmsModeDetailActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(SmsModeDetailActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			SmsModeDetailActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(SmsModeDetailActivity.this,
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
		private String toUserid = "";
		
		public CheckVIPTask(String toUserid) {
			this.toUserid = toUserid;
		}

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
			}
			sendMsg();
		}
		
	}
	
}
