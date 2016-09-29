package com.msg.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.bean.Group;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.ContactUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.UserManager;
import com.msg.utils.DialogUtil.DialogOnClickListener;

public class GroupActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private ListView mLvGroups;
	private GroupAdapter mAdapter;
	private static final int TYPE_SELECT = 0;
	private static final int TYPE_CHECK = 1;
	private ImageView mIbTitle, mIbReturn;
	private Button mBtnFinish, mBtnCancel;
	private boolean isEdit;
	private ArrayList<ContactGroup> mDelList = new ArrayList<ContactGroup>();

	private ArrayList<ContactGroup> mGroups = new ArrayList<ContactGroup>();

	private IMStorageDataBase mDb;
	private String mUid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_contact_groups);

		mUid = UserManager.getUserinfo(this).getUID();
		mDb = new IMStorageDataBase(this);
		mGroups = ContactUtils.getAllContactsByGroup(this);
		((TextView) findViewById(R.id.tv_title)).setText("分组列表");
		mIbTitle = (ImageView) findViewById(R.id.btn_title_del);
		mIbTitle.setVisibility(View.VISIBLE);
		mIbReturn = (ImageView) findViewById(R.id.btn_return);
		mIbReturn.setVisibility(View.VISIBLE);
		mIbReturn.setOnClickListener(this);
		mIbTitle.setOnClickListener(this);
		mBtnFinish = (Button) findViewById(R.id.btn_title_finish);
		mBtnFinish.setText("删除");
		mBtnFinish.setOnClickListener(this);
		mBtnCancel = (Button) findViewById(R.id.btn_title_return);
		mBtnCancel.setText("取消");
		mBtnCancel.setOnClickListener(this);
		mLvGroups = (ListView) findViewById(R.id.lv_groups);
		handlerChoose(false);
	}

	private class GroupAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private int type;

		public GroupAdapter(Context context, int type) {
			mInflater = LayoutInflater.from(context);
			this.type = type;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mGroups.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mGroups.get(position);
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
			ContactGroup cg = (ContactGroup) getItem(position);
			holder.tv_name.setText(cg.getName());
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
		case R.id.btn_title_del:
			handlerChoose(true);
			break;
		case R.id.btn_title_finish:
			DialogUtil.dialogMessage(GroupActivity.this, "", "将会删除此分组。", "确定",
					"", "取消", new DialogOnClickListener() {

						@Override
						public void onDialogClick(DialogInterface dialog,
								int whichButton, int source) {
							if (source == DialogUtil.SOURCE_POSITIVE) {
								if (mGroups.size() != 0) {
									for (ContactGroup cg : mGroups) {
										if (cg.isCheck()) {
											mDelList.add(cg);
										}
									}
									handlerDeleteGroupContacts();
								}
								handlerChoose(false);
							}
						}
					});
			break;
		case R.id.btn_title_return:
			handlerChoose(false);
			break;
		case R.id.btn_return:
			Intent intent = new Intent(this, ContactsActivity.class);
			setResult(RESULT_OK, intent);
			this.finish();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, ContactsActivity.class);
			setResult(RESULT_OK, intent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void handlerDeleteGroupContacts() {
		for (ContactGroup cg : mDelList) {
			mDb.delGroups(cg.getId() + "");
			mDb.delFriendByGroup(cg.getName(), mUid);
			mGroups.remove(cg);
		}
		mAdapter.notifyDataSetChanged();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			ArrayList<ContactGroup> cgs = (ArrayList<ContactGroup>) msg.obj;
			if (mGroups != null && mGroups.size() != 0) {
				mGroups.clear();
			}
			mGroups.addAll(cgs);
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, ContactsActivity.class);
		setResult(RESULT_OK, intent);
		this.finish();
	}

	private void handlerChoose(boolean show) {
		if (show) {
			isEdit = true;
			mAdapter = new GroupAdapter(this, TYPE_CHECK);
			mLvGroups.setAdapter(mAdapter);
			mIbReturn.setVisibility(View.GONE);
			mIbTitle.setVisibility(View.GONE);
			mBtnFinish.setVisibility(View.VISIBLE);
			mBtnCancel.setVisibility(View.VISIBLE);
			mLvGroups.setOnItemClickListener(this);
		} else {
			isEdit = false;
			mAdapter = new GroupAdapter(this, TYPE_SELECT);
			mLvGroups.setAdapter(mAdapter);
			mIbReturn.setVisibility(View.VISIBLE);
			mIbTitle.setVisibility(View.VISIBLE);
			mBtnFinish.setVisibility(View.GONE);
			mBtnCancel.setVisibility(View.GONE);
			mLvGroups.setOnItemClickListener(null);
			for (ContactGroup cg : mGroups) {
				cg.setCheck(false);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (isEdit) {
			ContactGroup cg = mGroups.get(arg2);
			boolean check = cg.isCheck();
			cg.setCheck(check ? false : true);
			mAdapter.notifyDataSetChanged();
		} else {
			Intent intent = new Intent(this, GroupItemActivity.class);
			intent.putExtra("index", arg2);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mAdapter.notifyDataSetChanged();
	}
}
