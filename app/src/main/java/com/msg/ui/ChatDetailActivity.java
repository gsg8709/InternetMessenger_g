package com.msg.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.feihong.msg.sms.R;
import com.msg.adapter.ChatAdapter;
import com.msg.bean.Contacts;
import com.msg.bean.ShowMsg;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.DialogUtil;
import com.msg.utils.DialogUtil.DialogOnClickListener;
import com.msg.widget.DragListView;
import com.msg.widget.PullToRefreshBase;
import com.msg.widget.PullToRefreshBase.OnRefreshListener;
import com.msg.widget.PullToRefreshListView;

/**
 * 聊天详情
 * @author gengsong
 *
 */
@SuppressWarnings("deprecation")
public class ChatDetailActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	private ArrayList<Contacts> mTo = new ArrayList<Contacts>();
	private List<ShowMsg> listMsg = new ArrayList<ShowMsg>();
	private IMStorageDataBase db;
	private DragListView listview;
	private ImApplication app;
	private ChatAdapter adapter;
	private int mPage = 1;
	private static final int LOAD_DATA = 1;
	private static final int REFREHS_DATA = 0;
	private StringBuilder mContacts = new StringBuilder("");
	private List<ShowMsg> mTempList = new ArrayList<ShowMsg>();
	private PullToRefreshListView mRefreshContainerView;
	private ClipboardManager mClipboard;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_chat);
		mClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		app = (ImApplication) getApplication();
		app.setChatRefreshHandler(mHandler);
		app.setVisibility(true);
		db = new IMStorageDataBase(this);
		((TextView) findViewById(R.id.tv_title)).setText("聊天");

		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		mRefreshContainerView = (PullToRefreshListView) findViewById(R.id.chatlist);
		mRefreshContainerView
				.setOnRefreshListener(new OnRefreshListener<DragListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<DragListView> refreshView) {
						mPage++;
						if (mTempList != null) {
							mTempList.clear();
						}

						if (mTo.size() == 1) {
							final ArrayList<ShowMsg> msgs = db
									.getMessageForJid(mTo.get(0).getTEL(),
											mPage);
							if (msgs != null && msgs.size() != 0) {
								mTempList.addAll(msgs);
								mTempList.addAll(listMsg);
								listMsg.clear();
								listMsg.addAll(mTempList);
								adapter.notifyDataSetChanged();
							}
							mRefreshContainerView.onRefreshComplete();
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									listview.setSelection(listMsg.size()
											- ((mPage - 1) * 10) - 1);
								}
							}, 50);
						}
					}
				});

		listview = mRefreshContainerView.getRefreshableView();
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		Bundle data = getIntent().getExtras();
		if (null != data) {
			mTo = (ArrayList<Contacts>) data.get("jid");
			for (Contacts contacts : mTo) {
				mContacts.append(contacts.getNAME()).append(",");
			}
			TextView tv = (TextView) findViewById(R.id.tv_title);
			if (mTo.size() > 1) {
				tv.setText("通知");
			} else {
				tv.setText(mContacts.substring(0, mContacts.length() - 1));
			}
		}

		mHandler.sendEmptyMessageDelayed(LOAD_DATA, 50);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_DATA:
				updateData();
				break;
			case REFREHS_DATA:
				updateData();
				break;
			}
		}
	};

	private void updateData() {
		if (mTo.size() == 1) {
			listMsg = db.getMessageForJid(mTo.get(0).getTEL(), mPage);
			refreshListView();
			for (int i = 0; i < listMsg.size(); i++) {
				ShowMsg msg = listMsg.get(i);
				msg.setMsgOpened(Configs.MSG_READED);
				db.updateMsgForRead(msg);
			}
		}
	}

	private void refreshListView() {
		Contacts user = new Contacts();
		if (mTo != null && mTo.size() != 0) {
			user = mTo.get(0);
		}
		this.adapter = new ChatAdapter(this, listMsg, user, mHandler);
		listview.setAdapter(adapter);
		listview.setSelection(listMsg.size());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			back();
			break;
		case R.id.btn_ok:
			Intent intent = new Intent(this, ChatSendActivity.class);
			intent.putExtra("jid", (Serializable) mTo);
			intent.putExtra("type", Configs.SEND_FROM_INTER_SMS);
			startActivityForResult(intent, 0);
			if (mTo.size() > 1) {
				this.finish();
			}
			break;
		case R.id.btn_cancel:
			Intent intent2 = new Intent(this, ChatSendActivity.class);
			intent2.putExtra("jid", (Serializable) mTo);
			intent2.putExtra("type", Configs.SEND_FROM_SMS);
			startActivityForResult(intent2, 0);
			if (mTo.size() > 1) {
				this.finish();
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			mHandler.sendEmptyMessage(REFREHS_DATA);
			break;
		case RESULT_FIRST_USER:
			Bundle bundle = data.getExtras();
			mTo = (ArrayList<Contacts>) bundle.getSerializable("result");
			if (mPopMsg.getMsgType() == Configs.MSG_TYPE_INTER_SMS) {
				Intent intent = new Intent(this, ChatSendActivity.class);
				intent.putExtra("jid", (Serializable) mTo);
				intent.putExtra("type", Configs.SEND_FROM_INTER_SMS);
				intent.putExtra("msg", mPopMsg);
				startActivityForResult(intent, 0);
			} else {
				Intent intent2 = new Intent(this, ChatSendActivity.class);
				intent2.putExtra("jid", (Serializable) mTo);
				intent2.putExtra("type", Configs.SEND_FROM_SMS);
				intent2.putExtra("msg", mPopMsg);
				startActivityForResult(intent2, 0);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void back() {
		Intent intent_send_inter_sms = new Intent(this,
				InterconnectionSmsActivity.class);
		setResult(RESULT_OK, intent_send_inter_sms);
		app.setChatRefreshHandler(null);
		this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ShowMsg msg = listMsg.get(arg2 - 1);
		
		if(msg.getMiniPic() != null) {
			String url = msg.getMiniPic().toString();
			if(!TextUtils.isEmpty(url)) {
				String tempurl[] = url.split("\\*");
				if(!TextUtils.isEmpty(tempurl[0]) && !"0".equals(tempurl[0]) && tempurl[1].equals("0")) {
					gotoNewChatPreviewPage(msg);
				} else if(msg.getMsg().contains("http")) {
					gotoNewChatPreviewPage(msg);
				} else if(Integer.parseInt(tempurl[0]) > 0 && Integer.parseInt(tempurl[1]) > 0 && Integer.parseInt(tempurl[1]) != 1) {
					gotoNewChatPreviewPage(msg);
				} else if(Integer.parseInt(tempurl[1]) == 1) {
					gotoChatPreviewPage(msg);
				} else {
					gotoChatPreviewPage(msg);
				}
			} else {
				if(msg.getMsg().contains("http")) {
					gotoNewChatPreviewPage(msg);
				} else {
					gotoChatPreviewPage(msg);
				}
				
			}
			
		} else {
			gotoChatPreviewPage(msg);
		}
		
		
	}

	/**
	 * 跳转到旧的回话展示页面
	 * @param msg
	 */
	private void gotoChatPreviewPage(ShowMsg msg) {
		Intent intent = new Intent(this, ChatPreviewActivity.class);
		intent.putExtra("msg", msg);
		intent.putExtra("name", mContacts.substring(0, mContacts.length() - 1));
		startActivity(intent);
	}
	
	/**
	 * 跳转到新的回话展示页面
	 * @param msg
	 */
	private void gotoNewChatPreviewPage(ShowMsg msg) {
		Intent intent = new Intent(this, ChatPreviewActivity2.class);
		intent.putExtra("msg", msg);
		intent.putExtra("name", mContacts.substring(0, mContacts.length() - 1));
		startActivity(intent);
	}
	
	private String[] mResClick = new String[] { "删除" };
	private ShowMsg mPopMsg;

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int arg2, long arg3) {
		DialogUtil.dialogList(this, "联系人", mResClick, "", "", "",
				new DialogUtil.DialogOnClickListener() {

					@Override
					public void onDialogClick(DialogInterface dialog,
							int whichButton, int source) {
						mPopMsg = listMsg.get(arg2 - 1);
						String title = mPopMsg.getTitle();
						String content = mPopMsg.getMsg();
						if (mPopMsg.getMsgType() == Configs.MSG_TYPE_INTER_SMS) {
							content = title + "\n" + content;
						}
						switch (whichButton) {
//						case 0:
//							Intent intent = new Intent(ChatDetailActivity.this,
//									ContactsChooseActivity.class);
//							intent.putExtra("page", Configs.FROM_SMS_PAGE);
//							startActivityForResult(intent, 0);
//							break;
						case 0:
							DialogUtil.dialogMessage(ChatDetailActivity.this,
									"", "是否确认删除", "确定", "", "取消",
									new DialogOnClickListener() {

										@Override
										public void onDialogClick(
												DialogInterface dialog,
												int whichButton, int source) {
											if (source == DialogUtil.SOURCE_POSITIVE) {
												db.delMessageForTime(mPopMsg);
												mHandler.sendEmptyMessage(REFREHS_DATA);
											}
										}
									});
							break;
						}
					}
				});
		return true;
	}
}
