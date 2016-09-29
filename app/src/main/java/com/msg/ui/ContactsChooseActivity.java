package com.msg.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.adapter.TreeViewAdapter2;
import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.ContactUtils;
import com.msg.utils.UserManager;

/**
 * 人脉圈
 * 
 * @author Chris
 * 
 */
public class ContactsChooseActivity extends Activity implements
		OnClickListener, OnItemClickListener, TextWatcher, OnChildClickListener {
	/**
	 * 通讯录列表
	 */
	private ExpandableListView mLvContacts;

	/**
	 * 通讯录列表适配器
	 */
	private TreeViewAdapter2 mAdapter;
	private int lastClick = -1;
	private ArrayList<Contacts> mChooseContacts = new ArrayList<Contacts>();
	private int mPage;
	private LinearLayout mLayoutProgress, mLayoutContacts, mLayoutSearch;
	private RelativeLayout mLayoutContent, mLayoutSearchShow;
	/**
	 * 搜索框
	 */
	private AutoCompleteTextView mEtSearch;
	private ImageView mImgCover;
	private TextView mTvCancel;
	private ImageView mIvReturn;
	private Button mIvFinish;
	private Button mBtnFinish;

	private List<Contacts> mSearchData = new ArrayList<Contacts>();
	private ArrayList<Contacts> mTempContacts = new ArrayList<Contacts>();

	private ArrayList<ContactGroup> mGroups = new ArrayList<ContactGroup>();
	private ArrayList<ContactGroup> mTempGroups = new ArrayList<ContactGroup>();
	private IMStorageDataBase mDb;
	private String mUid;
	private ArrayAdapter<String> mSearchAdapter;

	private FetchSearchSuggestionKeywordsAsyncTask fetchSearchSuggestionKeywordsAsyncTask;
	private int count = 0;
	private Map<Integer, Boolean> isSelected ; 
	private List beSelectedData = new ArrayList();	
	private int temp = -1;
	private HashMap<String, Boolean> statusHashMap; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_contacts_choose);
		mUid = UserManager.getUserinfo(this).getUID();
		mDb = new IMStorageDataBase(this);
		initView();
		refreshData();
		handlerData();
	}

	private void refreshData() {
		mTempContacts = mDb.getAllFriend(mUid);
		mTempGroups.add(new ContactGroup(0L, "联系人", mTempContacts.size(),
				mTempContacts));
		mTempGroups.addAll(ContactUtils.getAllContactsByGroup(this));
	}

	private void initView() {
		mIvReturn = (ImageView) findViewById(R.id.btn_return);
		mIvReturn.setVisibility(View.VISIBLE);
		mIvFinish = (Button) findViewById(R.id.btn_title_finish);
		mIvFinish.setVisibility(View.VISIBLE);
		mIvFinish.setText("确定");
		mIvFinish.setOnClickListener(this);
		mIvReturn.setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title)).setText("选择联系人");
		mLayoutContacts = (LinearLayout) findViewById(R.id.layout_contacts);
		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		mLayoutSearch = (LinearLayout) findViewById(R.id.layout_search_show);
		mLayoutSearch.setOnClickListener(this);
		mLayoutContent = (RelativeLayout) findViewById(R.id.layout_content);
		mLvContacts = (ExpandableListView) findViewById(R.id.list_contacts);
		mLvContacts.setOnItemClickListener(this);
		mLvContacts.setOnChildClickListener(this);
		mLvContacts.setGroupIndicator(null);

		mPage = getIntent().getExtras().getInt("page");

		mTvCancel = (TextView) findViewById(R.id.btn_searchbar_cancel);
		mTvCancel.setOnClickListener(this);
		mImgCover = (ImageView) findViewById(R.id.img_cover);
		mLayoutSearchShow = (RelativeLayout) findViewById(R.id.layout_contacts_search);
		mEtSearch = (AutoCompleteTextView) findViewById(R.id.searchbar_input_text);
		mEtSearch.addTextChangedListener(this);

		if (mPage == Configs.FROM_NOTE_PAGE) {
			mIvFinish.setVisibility(View.GONE);
		}

		if (mPage == Configs.FROM_SMS_PAGE) {
			mIvFinish.setVisibility(View.GONE);
			mBtnFinish = (Button) findViewById(R.id.btn_title_finish);
			mBtnFinish.setText("确定");
			mBtnFinish.setOnClickListener(this);
			mBtnFinish.setVisibility(View.VISIBLE);
		} else if (mPage == Configs.FROM_SMS_MODE_PAGE) {
			mIvFinish.setVisibility(View.GONE);
			mBtnFinish = (Button) findViewById(R.id.btn_title_finish);
//			mBtnFinish.setText("发送");
			mBtnFinish.setText("确定");
			mBtnFinish.setOnClickListener(this);
			mBtnFinish.setVisibility(View.VISIBLE);
		}
		
		
	}

	/**
	 * 开启/关闭联系人搜索动画
	 * 
	 * @param on
	 */
	private void searchAnimation(boolean on) {
		if (on) {
			final int top = mLayoutSearch.getTop();
			Animation translateAnimation = new TranslateAnimation(0f, 0f, 0,
					-top);
			translateAnimation.setDuration(300);
			translateAnimation.setFillAfter(true);
			mLayoutContacts.startAnimation(translateAnimation);

			translateAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mEtSearch.requestFocus();
					mEtSearch.setText("");
					AuxiliaryUtils.autoPopupSoftInput(
							ContactsChooseActivity.this, mEtSearch);
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
			AuxiliaryUtils.hideKeyboard(this, mEtSearch);
			mLayoutContacts.clearAnimation();
			mTvCancel.setVisibility(View.GONE);
			mLayoutContacts.setVisibility(View.VISIBLE);
			mLayoutSearchShow.setVisibility(View.GONE);

			mEtSearch.setText("");
		}
	}

	/**
	 * 处理通讯录列表数据显示刷新
	 * 
	 * @param cgs
	 */
	private void handlerData() {
		
		
		if (mGroups != null && mGroups.size() != 0) {
			mGroups.clear();
		}
		mGroups.addAll(mTempGroups);
		
		statusHashMap = new HashMap<String, Boolean>(); 

		for (int i = 0;i < mGroups.size(); i++) {// 初始时,让所有的子选项均未被选中 
			for (int j = 0; j < mGroups.get(i).contacts.size(); j++) {
				statusHashMap.put(mGroups.get(i).contacts.get(j).getNAME(), false);
			}
		}
		 
		if (mAdapter == null) {
			mAdapter = new TreeViewAdapter2(this, mGroups, mPage,statusHashMap);
			mLvContacts.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		mLvContacts.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (lastClick == -1) {
					mLvContacts.expandGroup(groupPosition);
				}

				if (lastClick != -1 && lastClick != groupPosition) {
					mLvContacts.collapseGroup(lastClick);
					mLvContacts.expandGroup(groupPosition);
				} else if (lastClick == groupPosition) {
					if (mLvContacts.isGroupExpanded(groupPosition))
						mLvContacts.collapseGroup(groupPosition);
					else if (!mLvContacts.isGroupExpanded(groupPosition))
						mLvContacts.expandGroup(groupPosition);
				}

				lastClick = groupPosition;
				return true;
			}
		});

		mLvContacts.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Contacts c = mGroups.get(groupPosition).getContacts()
						.get(childPosition);
				if (mPage == Configs.FROM_NOTE_PAGE) {
					ArrayList<Contacts> c_result = new ArrayList<Contacts>();
					c_result.add(c);
					handlerSearchChoose(c_result);
				} else {
					boolean ischeck = c.isCheck();
					c.setCheck(ischeck ? false : true);
//					mAdapter.notifyDataSetChanged();
					
					
					  int gourpsSum = mAdapter.getGroupCount();//组的数量 
			          for(int i = 0; i < gourpsSum; i++) { 
			              int childSum = mAdapter.getChildrenCount(i);//组中子项的数量 
			              for(int k = 0; k < childSum;k++) { 
			                  boolean isLast = false; 
			                  if (k == (childSum - 1)) {  
			                      isLast = true; 
			                  } 
			                     
			                  CheckBox cBox = (CheckBox) mAdapter.getChildView(i, k, isLast, null, null).findViewById(R.id.cb_child_contacts); 
			                  cBox.toggle();//切换CheckBox状态！！！！！！！！！！ 
			                  boolean itemIsCheck=cBox.isChecked(); 
			                  TextView tView=(TextView) mAdapter.getChildView(i, k, isLast, null, null).findViewById(R.id.tv_child_content); 
			                  String gameName=tView.getText().toString(); 
			                  if (i == groupPosition && k == childPosition) { 
			                      statusHashMap.put(gameName, itemIsCheck); 
			                  } else { 
			                       statusHashMap.put(gameName, false); 
			                  } 
			                  ((BaseExpandableListAdapter) mAdapter).notifyDataSetChanged();//通知数据发生了变化 
			              } 
			                 
			          }  
					
				}
				return false;
				
//				  int gourpsSum = mAdapter.getGroupCount();//组的数量 
//		          for(int i = 0; i < gourpsSum; i++) { 
//		              int childSum = mAdapter.getChildrenCount(i);//组中子项的数量 
//		              for(int k = 0; k < childSum;k++) { 
//		                  boolean isLast = false; 
//		                  if (k == (childSum - 1)) {  
//		                      isLast = true; 
//		                  } 
//		                     
//		                  CheckBox cBox = (CheckBox) mAdapter.getChildView(i, k, isLast, null, null).findViewById(R.id.cb_child_contacts); 
//		                  cBox.toggle();//切换CheckBox状态！！！！！！！！！！ 
//		                  boolean itemIsCheck=cBox.isChecked(); 
//		                  TextView tView=(TextView) mAdapter.getChildView(i, k, isLast, null, null).findViewById(R.id.tv_content); 
//		                  String gameName=tView.getText().toString(); 
//		                  if (i == groupPosition && k == childPosition) { 
//		                      statusHashMap.put(gameName, itemIsCheck); 
//		                  } else { 
//		                       statusHashMap.put(gameName, false); 
//		                  } 
//		                  ((BaseExpandableListAdapter) mAdapter).notifyDataSetChanged();//通知数据发生了变化 
//		              } 
//		                 
//		          }                
//		          return true; 
			}
		});
		dissmissDialog();
	}

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLayoutProgress.setVisibility(View.GONE);
				mLayoutSearch.setVisibility(View.VISIBLE);
				mLayoutContent.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_choose:
			handlerChoose();
			break;
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.layout_search_show:// 打开搜索
			searchAnimation(true);
			break;
		case R.id.btn_searchbar_cancel:// 关闭搜索
			searchAnimation(false);
			break;
		case R.id.btn_title_finish:
			handlerChoose();
			break;
		}
	}

	private void handlerChoose() {
		for (ContactGroup cg : mGroups) {
			for (Contacts c : cg.getContacts()) {
				if (c.isCheck()) {
					mChooseContacts.add(c);
				}
			}
		}

		Intent intent = new Intent();
		switch (mPage) {
		case Configs.FROM_SMS_PAGE:
			intent.setClass(this, InterconnectionSmsActivity.class);
			break;
		case Configs.FROM_CARD_PAGE:
			intent.setClass(this, MiniCardActivity.class);
			break;
		case Configs.FROM_NOTE_PAGE:
			intent.setClass(this, NoteAddActivity.class);
			break;
		case Configs.FROM_GOURP_PAGE:
			intent.setClass(this, GroupItemActivity.class);
			break;
		case Configs.FROM_SMS_MODE_PAGE:
			intent.setClass(this, SmsModeDetailActivity.class);
			break;
		}

		intent.putExtra("result", (Serializable) mChooseContacts);
		setResult(RESULT_FIRST_USER, intent);
		this.finish();
	}

	private void handlerSearchChoose(ArrayList<Contacts> c) {
		AuxiliaryUtils.hideKeyboard(ContactsChooseActivity.this, mEtSearch);
		Intent intent = new Intent();
		switch (mPage) {
		case Configs.FROM_SMS_PAGE:
			intent.setClass(this, InterconnectionSmsActivity.class);
			break;
		case Configs.FROM_CARD_PAGE:
			intent.setClass(this, MiniCardActivity.class);
			break;
		case Configs.FROM_NOTE_PAGE:
			intent.setClass(this, NoteAddActivity.class);
			break;
		case Configs.FROM_GOURP_PAGE:
			intent.setClass(this, GroupItemActivity.class);
			break;
		case Configs.FROM_SMS_MODE_PAGE:
			intent.setClass(this, SmsModeDetailActivity.class);
			break;
		}

		intent.putExtra("result", (Serializable) c);
		setResult(RESULT_FIRST_USER, intent);
		this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Contacts c = mContacts.get(arg2);
		// if (mPage == Configs.FROM_NOTE_PAGE) {
		// Intent intent = new Intent();
		// intent.setClass(this, NoteAddActivity.class);
		// mChooseContacts.add(c);
		// intent.putExtra("result", (Serializable) mChooseContacts);
		// setResult(RESULT_FIRST_USER, intent);
		// this.finish();
		// } else {
		// c.setCheck(c.isCheck() ? false : true);
		// mContactAdapter.notifyDataSetChanged();
		// }
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
		// TODO Auto-generated method stub

	}

	private class FetchSearchSuggestionKeywordsAsyncTask extends
			AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			List<String> rt = new ArrayList<String>();
			String keyword = mEtSearch.getText().toString();
			if (!TextUtils.isEmpty(keyword)) {
				try {
					mSearchData = mDb.getFriendsByKeyword(UserManager
							.getUserinfo(ContactsChooseActivity.this).getUID(),
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
						ContactsChooseActivity.this,
						R.layout.auto_complete_item, strings);
				mEtSearch.setAdapter(mSearchAdapter);
				mSearchAdapter.notifyDataSetChanged();
				mEtSearch.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						final Contacts c = mSearchData.get(arg2);
						ArrayList<Contacts> c_result = new ArrayList<Contacts>();
						c_result.add(c);
						handlerSearchChoose(c_result);
					}
				});
			}
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		return false;
	}
}
