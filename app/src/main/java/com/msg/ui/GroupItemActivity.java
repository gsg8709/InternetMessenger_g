package com.msg.ui;

import java.io.Serializable;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.ContactUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.UserManager;
import com.msg.utils.DialogUtil.DialogOnClickListener;

public class GroupItemActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {

	private static final int TYPE_SELECT = 0;
	private static final int TYPE_CHECK = 1;
	private ArrayList<Contacts> mContacts = new ArrayList<Contacts>();
	private ListView mLvContacts;
	private Contactsdapter mAdapter;
	private ImageView mIbAdd, mIbReturn;
	private int mIndex;;
	private ProgressDialog mDialog;
	private boolean isEdit;
	private ArrayList<ContactGroup> mGroups = new ArrayList<ContactGroup>();
	private IMStorageDataBase mDb;
	private String mUid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_group_item);
		mUid = UserManager.getUserinfo(this).getUID();
		mDb = new IMStorageDataBase(this);
		mGroups = ContactUtils.getAllContactsByGroup(this);
		mLvContacts = (ListView) findViewById(R.id.lv_contacts);
		mLvContacts.setOnItemClickListener(this);
		mLvContacts.setOnItemLongClickListener(this);
		Bundle data = getIntent().getExtras();
		if (data != null) {
			mIndex = data.getInt("index");
			((TextView) findViewById(R.id.tv_title)).setText(mGroups
					.get(mIndex).getName());
			mIbAdd = (ImageView) findViewById(R.id.btn_left_add);
			mIbAdd.setVisibility(View.VISIBLE);
			mIbAdd.setOnClickListener(this);
			mIbReturn = (ImageView) findViewById(R.id.btn_return);
			mIbReturn.setVisibility(View.VISIBLE);
			mIbReturn.setOnClickListener(this);
			mContacts = mGroups.get(mIndex).getContacts();

			mDialog = new ProgressDialog(this);
			mDialog.setMessage("刪除中，请稍后");
			handlerChoose(false);
		}
	}

	private void handlerChoose(boolean show) {
		mAdapter = new Contactsdapter(this, TYPE_SELECT);
		mLvContacts.setAdapter(mAdapter);
	}

	private class Contactsdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private int type;

		public Contactsdapter(Context context, int type) {
			mInflater = LayoutInflater.from(context);
			this.type = type;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mContacts.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mContacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.layout_group_edit_item, null);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.cb_group = (CheckBox) convertView
						.findViewById(R.id.cb_group);
				holder.iv = (ImageView) convertView.findViewById(R.id.img1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Contacts cg = (Contacts) getItem(position);
			holder.tv_name.setText(cg.getNAME());
			if (type == TYPE_CHECK) {
				holder.cb_group.setVisibility(View.VISIBLE);
				holder.iv.setVisibility(View.GONE);
				holder.cb_group.setChecked(cg.isCheck());
			} else {
				holder.cb_group.setVisibility(View.GONE);
				holder.iv.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		class ViewHolder {
			TextView tv_name;
			CheckBox cb_group;
			ImageView iv;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left_add:
			Intent intent_add = new Intent(this, ContactsChooseActivity.class);
			intent_add.putExtra("page", Configs.FROM_GOURP_PAGE);
			startActivityForResult(intent_add, 0);
			break;
		case R.id.btn_title_del:
			handlerChoose(true);
			break;
		case R.id.btn_title_finish:
			if (mContacts.size() != 0) {
				handlerDelContacts();
			} else {
				handlerChoose(false);
			}
			break;
		case R.id.btn_title_return:
			handlerChoose(false);
			break;
		case R.id.btn_ok:
			Intent intent = new Intent(this, ContactDetailActivity.class);
			intent.putExtra("type", Configs.CREATE_CONTACT);
			intent.putExtra("parent", mIndex);
			startActivityForResult(intent, 0);
			break;
		case R.id.btn_return:
			this.finish();
			break;
		}
	}

	private void handlerDelContacts() {
		for (Contacts cg : mContacts) {
			if (cg.isCheck()) {
				mDb.delFriend(cg);
			}
		}
		mGroups = ContactUtils.getAllContactsByGroup(this);
		mContacts = mGroups.get(mIndex).getContacts();
		handlerChoose(false);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		returnFlsh();
	}

	private void returnFlsh() {
		Intent intent = new Intent(this, GroupActivity.class);
		setResult(RESULT_OK, intent);
		this.finish();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_FIRST_USER:
			Bundle b = data.getExtras();
			ArrayList<Contacts> mListContacts = (ArrayList<Contacts>) b
					.getSerializable("result");
			if (mListContacts.size() != 0) {
				for (Contacts contacts : mListContacts) {
					contacts.setGROUPNAME(mGroups.get(mIndex).getName());
					mDb.saveFriendByGroup(contacts, mUid);
				}
				mGroups = ContactUtils.getAllContactsByGroup(this);
				mContacts = mGroups.get(mIndex).getContacts();
				mAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	private void delChildItem(Contacts contacts, int gindex) {
		mDb.delFriend(contacts);
		mGroups = ContactUtils.getAllContactsByGroup(this);
		mContacts = mGroups.get(mIndex).getContacts();
		mAdapter.notifyDataSetChanged();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (mGroups != null && mGroups.size() != 0) {
					mGroups.clear();
				}
				mGroups.addAll((ArrayList<ContactGroup>) msg.obj);
				mContacts = mGroups.get(mIndex).getContacts();
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int arg2, long arg3) {
		final Contacts c = (Contacts) mGroups.get(mIndex).getContacts()
				.get(arg2);
		DialogUtil.dialogMessage(this, "", "是否将其移除分该分组？", "是", "", "否",
				new DialogOnClickListener() {

					@Override
					public void onDialogClick(DialogInterface dialog,
							int whichButton, int source) {
						if (source == DialogUtil.SOURCE_POSITIVE) {
							delChildItem(c, 0);
						}
					}
				});
		return true;
	}

	private String[] mResClick = new String[] { "呼叫联系人", "发送短信", "查看联系人" };

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		if (isEdit) {
			Contacts cg = mContacts.get(arg2);
			boolean check = cg.isCheck();
			cg.setCheck(check ? false : true);
			mAdapter.notifyDataSetChanged();
		} else {
			final Contacts c = (Contacts) mGroups.get(mIndex).getContacts()
					.get(arg2);
			DialogUtil.dialogList(this, "联系人", mResClick, "", "", "",
					new DialogUtil.DialogOnClickListener() {

						@Override
						public void onDialogClick(DialogInterface dialog,
								int whichButton, int source) {
							switch (whichButton) {
							case 0:
								Intent intent = new Intent(Intent.ACTION_CALL,
										Uri.parse("tel:" + c.getTEL()));
								startActivity(intent);
								break;
							case 1:
								ArrayList<Contacts> jids = new ArrayList<Contacts>();
								jids.add(c);
								Intent intent_sms = new Intent(
										GroupItemActivity.this,
										ChatDetailActivity.class);
								intent_sms.putExtra("jid", (Serializable) jids);
								startActivity(intent_sms);
								break;
							case 2:
								Intent intent_detail = new Intent(
										GroupItemActivity.this,
										ContactDetailActivity.class);
								intent_detail.putExtra("type",
										Configs.QUERY_CONTACT);
								intent_detail.putExtra("contact", c);
								intent_detail.putExtra("parent", mIndex);
								startActivityForResult(intent_detail, 0);
								break;
							}
						}
					});
		}
	}
}
