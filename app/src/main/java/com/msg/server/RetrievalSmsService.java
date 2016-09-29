package com.msg.server;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.google.gson.Gson;
import com.msg.bean.Msg;
import com.msg.bean.RetrievalSmsListResult;
import com.msg.bean.RetrievalSmsResult;
import com.msg.bean.ShowMsg;
import com.msg.bean.User;
import com.msg.common.Configs;
import com.msg.ui.ImApplication;
import com.msg.ui.LoginActivity;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.NotificationUtils;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

@SuppressLint("HandlerLeak")
public class RetrievalSmsService extends Service implements HttpHandlerListener {

	private static final int RETRIEVAL_SMS = 0;
	private static final int GET_SMS_LIST = 1;
	private IMStorageDataBase db;
	private Handler refreshHandler;
	private Handler chatRefreshHandler;
	private Handler friendRefreshHandler;
	private ImApplication app;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
        handlerLogin();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		db = new IMStorageDataBase(this);
		app = (ImApplication) getApplication();
		retrievalSms();

		return START_STICKY;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case RETRIEVAL_SMS:
				retrievalSms();
				break;
			case GET_SMS_LIST:
				requestSmsList();
				break;
			}
		}
	};

	private void requestSmsList() {
		User user = UserManager.getUserinfo(getApplicationContext());
		if (user.getUID() != null) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UID", user.getUID()));
			NetHttpHandler handler = new NetHttpHandler(this);
			handler.setHttpHandlerListener(this);
			handler.execute(Configs.MSG_LIST_ADDRESS, HttpRequestType.POST,
					params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
		}
	}

	private void retrievalSms() {
		User user = UserManager.getUserinfo(getApplicationContext());
		if (null != user) {
			if (user.getUID() != null) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("UID", user.getUID()));
				params.add(new BasicNameValuePair("IMEI", Configs.imei));
				NetHttpHandler handler = new NetHttpHandler(this);
				handler.setHttpHandlerListener(this);
				handler.execute(Configs.MSG_NOREAD_ADDRESS,
						HttpRequestType.POST, params, true,
						NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN);
			} else {
				mHandler.sendEmptyMessageDelayed(RETRIEVAL_SMS, 10000);
			}
		}
	}


    private void handlerLogin() {
        User user = UserManager.getUserinfo(getApplicationContext());

        if (!TextUtils.isEmpty(user.getUID()) && !TextUtils.isEmpty(user.getPWD())) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("UID", user.getUID()));
            params.add(new BasicNameValuePair("PWD", AuxiliaryUtils.md5(user.getPWD())));
            params.add(new BasicNameValuePair("IMEI", Configs.imei));
            NetHttpHandler handler = new NetHttpHandler(this);
            handler.setHttpHandlerListener(this);
            handler.execute(Configs.USER_LOGIN_ADDRESS, HttpRequestType.POST,
                    params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
            Log.e("Goo", Configs.USER_LOGIN_ADDRESS + params.toString());
        } else {
        }
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(RETRIEVAL_SMS);
		mHandler.removeMessages(GET_SMS_LIST);
	}

	private void logout() {
		if (!UserManager.getRemeberPwd(this)) {
			UserManager.cleanUserInfo(this);
		}
		UserManager.saveLogout(this, true);
		UserManager.saveScreenPop(this, false);
		UserManager.saveOfHookSms(this, false);
		AuxiliaryUtils.closePage();
		if (app.isVisibility()) {
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			intent.putExtra("offline", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
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
			switch (mime) {
			case NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN:
				Gson gson = new Gson();
				RetrievalSmsResult result = gson.fromJson(new String(data),
						RetrievalSmsResult.class);
				if (result.isState()) {
					int count = result.getInfo();
					if (count > 0) {
						mHandler.sendEmptyMessage(GET_SMS_LIST);
					} else if (count == -1) {
						logout();
					} else {
						mHandler.sendEmptyMessageDelayed(RETRIEVAL_SMS, 10000);
					}
				} else {
					mHandler.sendEmptyMessageDelayed(RETRIEVAL_SMS, 10000);
				}
				break;
			case NetHttpHandler.RECEIVE_DATA_MIME_STRING:
				Gson gson_list = new Gson();
				RetrievalSmsListResult list = gson_list.fromJson(new String(
						data), RetrievalSmsListResult.class);
				ArrayList<Msg> msgs = list.getInfo();
				if (msgs != null && msgs.size() != 0) {
					for (Msg msg : msgs) {
						String name = db.getFriendNameOrInsertContact(msg
								.getSENDUID(), UserManager.getUserinfo(this)
								.getUID());
						ShowMsg show_msg = new ShowMsg(msg.getSENDUID(), name,
								msg.getUID(), Configs.MSG_TYPE_INTER_SMS,
								Configs.RECEIVE_MSG, Configs.MSG_UNREADED,
								msg.getIMAGE(), null, msg.getAUDIO(), null,
								msg.getTITLE(), msg.getMSG(),
								System.currentTimeMillis() + "","");
						db.saveMsg(show_msg);
					}
					Msg msg = msgs.get(0);
					String name = db.getFriendNameOrInsertContact(msg
							.getSENDUID(), UserManager.getUserinfo(this)
							.getUID());
					if (!app.isVisibility()) {
						NotificationUtils.notifi(getApplicationContext(), msg.getMSG(), msg.getSENDUID(), name);
					} else {
						NotificationUtils.notifi(getApplicationContext());
					}
					chatRefreshHandler = app.getChatRefreshHandler();
					refreshHandler = app.getRefreshHandler();
					if (chatRefreshHandler != null) {
						chatRefreshHandler.sendEmptyMessage(0);
					} else {
						refreshHandler.sendEmptyMessage(0);
					}

//					friendRefreshHandler = app.getFriendRefreshHandler();
//					if (friendRefreshHandler != null) {
//						friendRefreshHandler.sendEmptyMessage(1);
//					}
				}
				mHandler.sendEmptyMessageDelayed(RETRIEVAL_SMS, 10000);
				break;
			}
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			mHandler.sendEmptyMessageDelayed(RETRIEVAL_SMS, 10000);
			break;
		default:
			mHandler.sendEmptyMessageDelayed(RETRIEVAL_SMS, 10000);
			break;
		}
	}
}
