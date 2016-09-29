package com.msg.ui;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.adapter.ContactAdapter;
import com.msg.adapter.TreeViewAdapter;
import com.msg.adapter.TreeViewAdapter.OnHandlerListener;
import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.bean.GroupParent;
import com.msg.bean.SyncResult;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.ContactUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.DialogUtil.DialogOnClickListener;
import com.msg.utils.UserManager;
import com.msg.widget.MListView;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 人脉圈
 * 
 * @author Chris
 * 
 */
public class ContactsActivity extends Activity implements OnClickListener,
		HttpHandlerListener, OnItemClickListener, OnItemLongClickListener,
		OnHandlerListener, OnChildClickListener, TextWatcher {
	/**
	 * 通讯录列表
	 */
	private ExpandableListView mLvGroups;

	private MListView mLvContacts;
	/**
	 * 通讯录列表适配器
	 */
	private TreeViewAdapter mAdapter;
	private ContactAdapter mContactAdapter;
	private LinearLayout mLayoutContacts, mLayoutProgress;
	private RelativeLayout mLayoutSearchShow;
	/**
	 * 搜索框
	 */
	private AutoCompleteTextView mEtSearch;
	private ArrayAdapter<String> mSearchAdapter;
	private List<Contacts> mSearchData = new ArrayList<Contacts>();
	private ImageView mImgCover, mIvImport;
	private TextView mTvCancel;
	private ProgressDialog mDialog;

	private IMStorageDataBase mDb;
	private ArrayList<ContactGroup> mGroups = new ArrayList<ContactGroup>();
	private ArrayList<ContactGroup> mTempGroups = new ArrayList<ContactGroup>();
	private ArrayList<Contacts> mContacts = new ArrayList<Contacts>();
	private ArrayList<Contacts> mTempContacts = new ArrayList<Contacts>();
	private ArrayList<GroupParent> mGroupParent = new ArrayList<GroupParent>();
	private String mUid;
	private ImApplication mApp;
    private LinearLayout layout_search_show;
	private FetchSearchSuggestionKeywordsAsyncTask fetchSearchSuggestionKeywordsAsyncTask;
	private TextView search_edittext;
	private String before = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_contacts);
		mUid = UserManager.getUserinfo(this).getUID();
		mDb = new IMStorageDataBase(this);
		mApp = (ImApplication) getApplication();
		mApp.setFriendRefreshHandler(mHandler);
		initView();
		refreshData();
		handlerContacts();
	}

	private void refreshData() {
		mTempGroups = ContactUtils.getAllContactsByGroup(this);
		mTempContacts = mDb.getAllFriend(mUid);
		if (mGroupParent.size() != 0) {
			mGroupParent.clear();
		}
		mGroupParent.add(new GroupParent("群组", mDb.getAllGroups()));
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		((TextView) findViewById(R.id.tv_title)).setText("人脉圈");
		mIvImport = (ImageView) findViewById(R.id.btn_title_right);
		mIvImport.setVisibility(View.VISIBLE);
		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		mLayoutContacts = (LinearLayout) findViewById(R.id.layout_contacts);
		mLvGroups = (ExpandableListView) findViewById(R.id.list_contacts);
		mLvGroups.setGroupIndicator(null);
		layout_search_show = (LinearLayout) this.findViewById(R.id.layout_search_show);
		layout_search_show.setOnClickListener(this);
		LinearLayout layout_head = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_contacts_list_head, null);
		RelativeLayout layout_add_contacts = (RelativeLayout) layout_head
				.findViewById(R.id.layout_add_contacts);
		layout_add_contacts.setOnClickListener(this);
		mLvGroups.addHeaderView(layout_head);
		// mLvGroups.setOnChildClickListener(this);

		LinearLayout layout_foot = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_contacts_list_foot, null);
		mLvContacts = (MListView) layout_foot.findViewById(R.id.lv_contacts);
		mLvGroups.addFooterView(layout_foot);
		mLvContacts.setOnItemClickListener(this);
		mLvContacts.setOnItemLongClickListener(this);

		mTvCancel = (TextView) findViewById(R.id.btn_searchbar_cancel);
		mTvCancel.setOnClickListener(this);
		mImgCover = (ImageView) findViewById(R.id.img_cover);
		mLayoutSearchShow = (RelativeLayout) findViewById(R.id.layout_contacts_search);
		mEtSearch = (AutoCompleteTextView) findViewById(R.id.searchbar_input_text);
		mEtSearch.addTextChangedListener(this);

		mDialog = new ProgressDialog(this);
		mDialog.setMessage("同步中，请稍后");
		
		search_edittext = (TextView) this.findViewById(R.id.search_edittext);
		search_edittext.setOnClickListener(this);
	}

	private void queryContacts(Contacts c) {
		Intent intent = new Intent(this, ContactDetailActivity.class);
		intent.putExtra("type", Configs.QUERY_CONTACT);
		intent.putExtra("contact", c);
		startActivity(intent);

	}

	private void updateContacts(Contacts c) {
		Intent intent = new Intent(this, ContactDetailActivity.class);
		intent.putExtra("type", Configs.EDIT_CONTACT);
		intent.putExtra("contact", c);
		intent.putExtra("parent", 0);
		startActivityForResult(intent, 0);
	}

	private void delChildItem(int gindex) {
		mDb.delFriendByNameAndTel(mContacts.get(gindex), mUid);
		mContacts.remove(gindex);
		refreshData();
		handlerContacts();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_searchbar_cancel:// 关闭搜索
			searchAnimation(false);
			break;
		case R.id.layout_add_contacts:// 新建联系人
			addContacts();
			break;
		case R.id.layout_search_show://打开搜索
			searchAnimation(true);
			break;
		case R.id.search_edittext://打开搜索
			searchAnimation(true);
			break;
		case R.id.btn_title_right:// 导入通讯录
			DialogUtil.dialogMessage(this, "导入手机通讯录", null, "确定", "", "取消",
					new DialogOnClickListener() {

						@Override
						public void onDialogClick(DialogInterface dialog,
								int whichButton, int source) {
							switch (source) {
							case DialogUtil.SOURCE_POSITIVE:
								mDialog.show();
								ContactUtils.handlerMacContacts(
										ContactsActivity.this, mHandler);
								UserManager.saveFirstLogin(
										ContactsActivity.this, false);
								break;
							}
						}
					});
			break;
		}
	}

	/**
	 * 新建联系人
	 */
	private void addContacts() {
		Intent intent = new Intent(this, ContactDetailActivity.class);
		intent.putExtra("type", Configs.CREATE_CONTACT);
		intent.putExtra("parent", 0);
		startActivityForResult(intent, 0);
	}

	/**
	 * 新建联系人回调刷新
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		refreshData();
		handlerContacts();
	}

	/**
	 * 处理所有机器联系人信息
	 */
	private void handlerContacts() {
		if (mGroups != null && mGroups.size() != 0) {
			mGroups.clear();
		}
		mGroups.addAll(mTempGroups);
		initList();
	}
	

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				syncContacts();
				break;
			case 1:
				refreshData();
				handlerContacts();
				break;
			case 4:
				searchAnimation(false);
				break;
			}
		}
	};

	private class FetchSearchSuggestionKeywordsAsyncTask extends
			AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			List<String> rt = new ArrayList<String>();
			String keyword = mEtSearch.getText().toString();
			if (!TextUtils.isEmpty(keyword)) {
				try {
					mSearchData = mDb.getFriendsByKeyword(UserManager
							.getUserinfo(ContactsActivity.this).getUID(),
							AuxiliaryUtils.getFullSpell(keyword.toString()));
					for (Contacts c : mSearchData) {
						rt.add(c.getNAME());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return rt;
		}

		@Override
		protected void onPostExecute(List<String> strings) {
			super.onPostExecute(strings);
			if (!strings.isEmpty()) {
				mSearchAdapter = new ArrayAdapter<String>(
						ContactsActivity.this, R.layout.auto_complete_item,
						strings);
				mEtSearch.setAdapter(mSearchAdapter);
				mSearchAdapter.notifyDataSetChanged();
				mEtSearch.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						final Contacts c = mSearchData.get(arg2);
						DialogUtil.dialogList(ContactsActivity.this, "联系人",
								mResClick, "", "", "",
								new DialogUtil.DialogOnClickListener() {

									@Override
									public void onDialogClick(
											DialogInterface dialog,
											int whichButton, int source) {
										switch (whichButton) {
										case 0:
											Intent intent = new Intent(
													Intent.ACTION_CALL,
													Uri.parse("tel:"
															+ c.getTEL()));
											startActivity(intent);
											break;
										case 1:
											ArrayList<Contacts> jids = new ArrayList<Contacts>();
											jids.add(c);
											Intent intent_sms = new Intent(
													ContactsActivity.this,
													ChatDetailActivity.class);
											intent_sms.putExtra("jid",
													(Serializable) jids);
											startActivity(intent_sms);
											break;
										case 2:
											queryContacts(c);
											break;
										}
										mHandler.sendEmptyMessageDelayed(4, 200);
									}
								});
					}
				});
			}
		}
	}

	ArrayList<ContactGroup> list = new ArrayList<ContactGroup>();
	ArrayList<Contacts> list_foot = new ArrayList<Contacts>();

	private void initList() {
		if (mAdapter == null) {
			mAdapter = new TreeViewAdapter(this, mGroupParent, mLvGroups);
			mLvGroups.setAdapter(mAdapter);
			mAdapter.setOnHandlerListener(this);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		if (mContacts != null && mContacts.size() != 0) {
			mContacts.clear();
		}
		mContacts.addAll(mTempContacts);

		if (mContactAdapter == null) {
			mContactAdapter = new ContactAdapter(this, mContacts);
			mLvContacts.setAdapter(mContactAdapter);
		} else {
			mContactAdapter.notifyDataSetChanged();
		}
		mHandler.sendEmptyMessageDelayed(0, 200);
		dissmissDialog();
	}

	/**
	 * 同步上传联系人
	 */
	private void syncContacts() {
		if (mContacts.size() == 0) {
			dissmissDialog();
			return;
		}

		String uid = UserManager.getUserinfo(this).getUID();
		String json = handlerJson(uid);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("UID", uid));
		params.add(new BasicNameValuePair("JSON", json));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.CONTACT_SYNC_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	/**
	 * 拼凑联系人json
	 * 
	 * @param json
	 * @param uid
	 * @param all_contacts
	 */
	private String handlerJson(String uid) {
		StringBuilder json = new StringBuilder("");
		json.append("{\"info\":[");
		for (Contacts c : mContacts) {
			String name = c.getNAME();
			String num = c.getTEL();
			if (TextUtils.isEmpty(name)) {
				name = "";
			} else {
				name = "\"" + c.getNAME() + "\"";
			}
			if (TextUtils.isEmpty(name)) {
				num = "";
			} else {
				num = "\"" + c.getTEL() + "\"";
			}
			json.append("{").append("\"CID\":").append(c.getContactId())
					.append(",").append("\"NAME\":").append(name).append(",")
					.append("\"GROUPNAME\":")
					.append("\"" + c.getGROUPNAME() + "\"").append(",")
					.append("\"UID\":").append("\"" + uid + "\"").append(",")
					.append("\"TEL\":").append(num).append(",")
					.append("\"CTIME\":")
					.append("\"" + System.currentTimeMillis() + "\"")
					.append("}").append(",");
		}
		return json.substring(0, json.length() - 1) + "]}";
	}

	/**
	 * 开启/关闭联系人搜索动画
	 * 
	 * @param on
	 */
	private void searchAnimation(final boolean on) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (on) {
					final int top = layout_search_show.getTop();
					Animation translateAnimation = new TranslateAnimation(0f,0f, 0, -top);
					translateAnimation.setDuration(300);
					translateAnimation.setFillAfter(true);
					
					mLayoutContacts.startAnimation(translateAnimation);

					translateAnimation
							.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationRepeat(Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									mEtSearch.requestFocus();
									mEtSearch.setText(before);
									AuxiliaryUtils.autoPopupSoftInput(ContactsActivity.this, mEtSearch);
									mLayoutContacts.clearAnimation();
									mLayoutContacts.setVisibility(View.GONE);
									mTvCancel.setVisibility(View.VISIBLE);
									mLayoutSearchShow.setVisibility(View.VISIBLE);
									Animation alphaAnimation = new AlphaAnimation(0, 1.0f);
									alphaAnimation.setDuration(300);
									alphaAnimation.setFillAfter(true);
									mImgCover.startAnimation(alphaAnimation);
								}
							});
				} else {
					AuxiliaryUtils.hideKeyboard(ContactsActivity.this,
							mEtSearch);
					mLayoutContacts.clearAnimation();
					mTvCancel.setVisibility(View.GONE);
					mLayoutContacts.setVisibility(View.VISIBLE);
					mLayoutSearchShow.setVisibility(View.GONE);
					mEtSearch.setText("");
				}
			}
		});
	}

	/**
	 * 网络结果返回处理
	 */
	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			byte[] data, int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			switch (mime) {
			case NetHttpHandler.RECEIVE_DATA_MIME_STRING: // 同步
				Gson gson = new Gson();
				SyncResult result = gson.fromJson(new String(data),
						SyncResult.class);
				handlerSyncResult(result);
				dissmissDialog();
				break;
			}
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(ContactsActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			/*runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(ContactsActivity.this,
							R.string.msg_network_error);
				}
			});*/
			dissmissDialog();
			break;
		}
	}

	private String[] mResClick = new String[] { "呼叫联系人", "发送短信", "查看联系人" };

	/**
	 * 同步结果
	 * 
	 * @param result
	 */
	private void handlerSyncResult(SyncResult result) {
		Log.e("Goo", "result:" + result);
	}

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
				mIvImport.setOnClickListener(ContactsActivity.this);
				mLayoutProgress.setVisibility(View.GONE);
				layout_search_show.setVisibility(View.VISIBLE);
				mLvGroups.setVisibility(View.VISIBLE);
				findViewById(R.id.btn_title).setOnClickListener(
						ContactsActivity.this);
			}
		});
	}

	private String[] mResLongClick = new String[] { "编辑联系人", "删除联系人", };

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int arg2, long arg3) {
		final Contacts c = mContacts.get(arg2);
		DialogUtil.dialogList(this, "联系人", mResLongClick, "", "", "",
				new DialogUtil.DialogOnClickListener() {

					@Override
					public void onDialogClick(DialogInterface dialog,
							int whichButton, int source) {
						switch (whichButton) {
						case 0:
							updateContacts(c);
							break;
						case 1:
							DialogUtil.dialogMessage(ContactsActivity.this, "",
									"是否确认删除", "确定", "", "取消",
									new DialogOnClickListener() {

										@Override
										public void onDialogClick(
												DialogInterface dialog,
												int whichButton, int source) {
											if (source == DialogUtil.SOURCE_POSITIVE) {
												delChildItem(arg2);
											}
										}
									});
							break;
						}
					}
				});
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		final Contacts c = (Contacts) mContacts.get(arg2);
		DialogUtil.dialogList(this, "联系人", mResClick, "", "", "",
				new DialogUtil.DialogOnClickListener() {

					@Override
					public void onDialogClick(DialogInterface dialog,
							int whichButton, int source) {
						switch (whichButton) {
						case 0:
							Intent intent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel:" + c.getTEL()));
							startActivity(intent);
							break;
						case 1:
							ArrayList<Contacts> jids = new ArrayList<Contacts>();
							jids.add(c);
							Intent intent_sms = new Intent(
									ContactsActivity.this,
									ChatDetailActivity.class);
							intent_sms.putExtra("jid", (Serializable) jids);
							startActivity(intent_sms);
							break;
						case 2:
							queryContacts(c);
							break;
						}
					}
				});
	}

	@Override
	public void onDelete() {
		Intent intent = new Intent(this, GroupActivity.class);
		intent.putExtra("data", (Serializable) mGroups);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onCreate() {
		refreshData();
		handlerContacts();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Intent intent = new Intent(this, GroupItemActivity.class);
		intent.putExtra("index", childPosition);
		startActivityForResult(intent, 0);
		return false;
	}
	
	

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() >= 1) {
			if (fetchSearchSuggestionKeywordsAsyncTask != null) {
				fetchSearchSuggestionKeywordsAsyncTask.cancel(true);
			}
			fetchSearchSuggestionKeywordsAsyncTask = new FetchSearchSuggestionKeywordsAsyncTask();
			fetchSearchSuggestionKeywordsAsyncTask.execute();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
