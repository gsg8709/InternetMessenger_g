package com.msg.ui;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.msg.server.RetrievalSmsService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.adapter.MessageListAdapter;
import com.msg.bean.Contacts;
import com.msg.bean.ShowMsg;
import com.msg.bean.User;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.DialogUtil.DialogOnClickListener;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 互联短信
 * 
 * @author gengsong
 * 
 */
public class InterconnectionSmsActivity extends Activity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener, HttpHandlerListener {
	private ListView mLvSms;
	private IMStorageDataBase mDb;
	private ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>> mRefreshMsgs = new ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>>();
	private MessageListAdapter mAdapter;
	private ImApplication app;
	private String mUid;
	private TextView numIcon;
	private RelativeLayout sys_info_relout;
	public static InterconnectionSmsActivity INSTANCE;
	private onRefrehsMsgListener mListener;

	public interface onRefrehsMsgListener {
		void onRefresh(int num);
	}

	public void setonRefrehsMsgListener(onRefrehsMsgListener list) {
		mListener = list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_interconnection_sms);
		INSTANCE = this;
		mUid = UserManager.getUserinfo(this).getUID();
		Intent intent = this.getIntent();
		((TextView) findViewById(R.id.tv_title)).setText("互联短信");
		findViewById(R.id.btn_title).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_title).setOnClickListener(this);

		mLvSms = (ListView) findViewById(R.id.list_sms);
		
		mLvSms.setOnItemClickListener(this);
		mLvSms.setOnItemLongClickListener(this);
		mDb = new IMStorageDataBase(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		app = (ImApplication) getApplication();
		app.setRefreshHandler(refreshHandler);
		refreshHandler.sendEmptyMessage(0);

		mDb.initWelcome(this, mUid);
		refreshHandler.sendEmptyMessage(0);
        startService(new Intent(this, RetrievalSmsService.class));
	}

	@SuppressLint("HandlerLeak")
	private Handler refreshHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>> msg_show = mDb
					.getAllMessage(mUid);
			refreshList(msg_show);
			refreshTabNumText();
			/**
			 * 再一次调用登陆接口，给后台统计用户在线时间
			 */
			handlerLogin();
		}
	};

	/**
	 * 刷新tab上num显示
	 * 
	 * @param msg_show
	 */
	private void refreshTabNumText() {
		if (mListener != null) {
			int count = mDb.getUnreadMessageCount(UserManager.getUserinfo(this)
					.getUID());
			mListener.onRefresh(count);
		}
	}

	/**
	 * 刷新消息列表 取发送人user
	 */
	private void refreshList(
			ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>> msg) {
		if (mRefreshMsgs.size() != 0) {
			mRefreshMsgs.clear();
		}
		mRefreshMsgs.addAll(msg);
		if (mRefreshMsgs.size() > 0) {
			if (mAdapter == null) {
				mAdapter = new MessageListAdapter(this, mRefreshMsgs);
				mLvSms.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title:
			Intent intent = new Intent(this, ContactsChooseActivity.class);
			intent.putExtra("page", Configs.FROM_SMS_PAGE);
			startActivityForResult(intent, 0);
			break;
		case R.id.sys_info_relout:
			Intent intent2 = new Intent(this, ChatDetailActivity.class);
//			intent2.putExtra("jid", (Serializable) jids);
			startActivityForResult(intent2, 0);
			break;
		}
	}

	private ArrayList<Contacts> mListContacts = new ArrayList<Contacts>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_FIRST_USER:
			Bundle b = data.getExtras();
			mListContacts = (ArrayList<Contacts>) b.getSerializable("result");
			if (mListContacts.size() != 0) {
				Intent intent = new Intent(this, ChatDetailActivity.class);
				intent.putExtra("jid", (Serializable) mListContacts);
				startActivityForResult(intent, 0);
			}
			break;
		case RESULT_OK:
			refreshHandler.sendEmptyMessage(0);
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
			showLongClickDialog(position);
		return false;
	}

	private void showLongClickDialog(int position) {
		if (mRefreshMsgs.size() != 0) {
			final HashMap<Contacts, ArrayList<ShowMsg>> map = mRefreshMsgs
					.get(position);
			final Contacts user = (Contacts) map.keySet().toArray()[0];
			final String username = user.getTEL();
			String nickname = user.getNAME();
			if (TextUtils.isEmpty(nickname)
					|| nickname.equalsIgnoreCase("null")) {
				nickname = username;
			}
			String[] choices;
			int show = user.getSHOW();
			if (show == 1) {
				choices = new String[] { "删除会话", "添加到人脉圈" };
			} else {
				choices = new String[] { "删除会话" };
			}
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle(nickname)
					.setItems(
							choices,
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										DialogUtil
												.dialogMessage(
														InterconnectionSmsActivity.this,
														"",
														"是否确认删除",
														"确定",
														"",
														"取消",
														new DialogOnClickListener() {

															@Override
															public void onDialogClick(
																	DialogInterface dialog,
																	int whichButton,
																	int source) {
																if (source == DialogUtil.SOURCE_POSITIVE) {
																	mDb.delMessageForJid(username);
																	mRefreshMsgs
																			.remove(map);
																	mAdapter.notifyDataSetChanged();
																}
															}
														});
										break;
									case 1:
										Intent intent = new Intent(
												InterconnectionSmsActivity.this,
												ContactDetailActivity.class);
										intent.putExtra(
												"type",
												Configs.CREATE_CONTACT_BY_STRANGE_NUMBER);
										intent.putExtra("parent", -1);
										intent.putExtra("contact", user);
										startActivityForResult(intent, 0);
										break;
									}
								}
							}).create();
			dialog.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HashMap<Contacts, ArrayList<ShowMsg>> map = mRefreshMsgs.get(arg2);
		Contacts user = (Contacts) map.keySet().toArray()[0];
		ArrayList<Contacts> jids = new ArrayList<Contacts>();
		jids.add(user);
		Intent intent = new Intent(this, ChatDetailActivity.class);
		intent.putExtra("jid", (Serializable) jids);
		startActivityForResult(intent, 0);
		
	}
	
	private void handlerLogin() {
		User user = UserManager.getUserinfo(InterconnectionSmsActivity.this);
		
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
	public void onResponse(int responseCode, String responseStatusLine,
			byte[] data, int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			break;
		default:
			break;
		}
	}

}
