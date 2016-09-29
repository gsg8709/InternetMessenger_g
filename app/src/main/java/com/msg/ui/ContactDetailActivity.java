package com.msg.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.common.Configs;
import com.msg.server.IMStorageDataBase;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.ContactUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.DialogUtil.DialogOnClickListener;
import com.msg.utils.UserManager;

public class ContactDetailActivity extends Activity implements OnClickListener {
	private EditText mEtName, mEtNum, mEtCompany, mEtBranch, mEtJob;
	private TextView mEtBirthday;
	private int mIndex, mParent = -1;
	private IMStorageDataBase mDb;
	private Contacts mContacts;
	private String mUid;
	private RadioButton mRbMale, mRbFamale;
	private Handler friendRefreshHandler;
	private ImApplication app;

	private ArrayList<ContactGroup> mGroups = new ArrayList<ContactGroup>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_add_contacts);
		app = (ImApplication) getApplication();
		friendRefreshHandler = app.getFriendRefreshHandler();
		mDb = new IMStorageDataBase(this);
		mUid = UserManager.getUserinfo(this).getUID();
		mGroups = ContactUtils.getAllContactsByGroup(this);
		initView();
		initData();
	}

	private void initData() {
		Bundle data = getIntent().getExtras();
		if (data != null) {
			mIndex = data.getInt("type");
			switch (mIndex) {
			case Configs.CREATE_CONTACT_BY_STRANGE_NUMBER:
				((TextView) findViewById(R.id.tv_title)).setText("新建联系人");
				mParent = data.getInt("parent");
				mContacts = (Contacts) data.get("contact");
				String name_s = mContacts.getNAME();
				String num_s = mContacts.getTEL();
				String brithday_s = mContacts.getBRITHDAY();
				String company_s = mContacts.getCOMPANY();
				String branch_s = mContacts.getBRANCH();
				String job_s = mContacts.getJOB();
				int sex_s = mContacts.getSEX();
				if (sex_s == 0) {
					mRbFamale.setChecked(true);
					mRbMale.setChecked(false);
				} else {
					mRbFamale.setChecked(false);
					mRbMale.setChecked(true);
				}
				mEtName.setText(name_s);
				mEtNum.setText(num_s);
				mEtName.setSelection(name_s.length());
				mEtBirthday.setText(brithday_s);
				mEtCompany.setText(company_s);
				mEtBranch.setText(branch_s);
				mEtJob.setText(job_s);
				findViewById(R.id.layout_groups).setOnClickListener(this);
				findViewById(R.id.layout_birthday).setOnClickListener(this);
				AuxiliaryUtils.autoPopupSoftInput(this, mEtName);
				break;
			case Configs.CREATE_CONTACT:
				((TextView) findViewById(R.id.tv_title)).setText("新建联系人");
				if (mGroups.size() > 0 && mParent != -1) {
					ContactGroup cg_new = mGroups.get(mParent);
					((TextView) findViewById(R.id.et_group)).setText(cg_new
							.getName());
					((TextView) findViewById(R.id.et_group)).setEnabled(false);
				}
				findViewById(R.id.layout_groups).setOnClickListener(this);
				findViewById(R.id.layout_birthday).setOnClickListener(this);
				AuxiliaryUtils.autoPopupSoftInput(this, mEtName);
				break;
			case Configs.EDIT_CONTACT:
				((TextView) findViewById(R.id.tv_title)).setText("编辑联系人");
				mParent = data.getInt("parent");
				mContacts = (Contacts) data.get("contact");
				String name = mContacts.getNAME();
				String num = mContacts.getTEL();
				String gname = mContacts.getGROUPNAME();
				String brithday = mContacts.getBRITHDAY();
				String company = mContacts.getCOMPANY();
				String branch = mContacts.getBRANCH();
				String job = mContacts.getJOB();
				int sex = mContacts.getSEX();
				if (!gname.equals("联系人")) {
					((TextView) findViewById(R.id.et_group)).setText(mContacts
							.getGROUPNAME());
				}
				if (sex == 0) {
					mRbFamale.setChecked(true);
					mRbMale.setChecked(false);
				} else {
					mRbFamale.setChecked(false);
					mRbMale.setChecked(true);
				}
				mEtName.setText(name);
				mEtNum.setText(num);
				mEtName.setSelection(name.length());
				mEtBirthday.setText(brithday);
				mEtCompany.setText(company);
				mEtBranch.setText(branch);
				mEtJob.setText(job);
				findViewById(R.id.layout_groups).setOnClickListener(this);
				findViewById(R.id.layout_birthday).setOnClickListener(this);
				AuxiliaryUtils.autoPopupSoftInput(this, mEtName);
				break;
			case Configs.QUERY_CONTACT:
				((TextView) findViewById(R.id.tv_title)).setText("查看联系人");
				mParent = data.getInt("parent");
				mContacts = (Contacts) data.get("contact");
				String name_q = mContacts.getNAME();
				String num_q = mContacts.getTEL();
				String gname_q = mContacts.getGROUPNAME();
				String brithday_q = mContacts.getBRITHDAY();
				String company_q = mContacts.getCOMPANY();
				String branch_q = mContacts.getBRANCH();
				String job_q = mContacts.getJOB();
				int sex_q = mContacts.getSEX();
				if (!gname_q.equals("联系人")) {
					((TextView) findViewById(R.id.et_group)).setText(gname_q);
				}
				if (sex_q == 0) {
					mRbFamale.setChecked(true);
					mRbMale.setChecked(false);
				} else {
					mRbFamale.setChecked(false);
					mRbMale.setChecked(true);
				}
				mEtName.setText(name_q);
				mEtNum.setText(num_q);
				mEtName.setSelection(name_q.length());
				mEtBirthday.setText(brithday_q);
				mEtCompany.setText(company_q);
				mEtBranch.setText(branch_q);
				mEtJob.setText(job_q);
				findViewById(R.id.btn_title_save).setVisibility(View.GONE);
				mEtName.setEnabled(false);
				mEtNum.setEnabled(false);
				AuxiliaryUtils.hideKeyboard(this, mEtName);
				break;
			}
		} else {
			findViewById(R.id.layout_groups).setOnClickListener(this);
		}
	}

	private void initView() {
		mEtName = (EditText) findViewById(R.id.et_name);
		mEtNum = (EditText) findViewById(R.id.et_tel);
		mEtBirthday = (TextView) findViewById(R.id.et_birthday);
		mEtCompany = (EditText) findViewById(R.id.et_company);
		mEtBranch = (EditText) findViewById(R.id.et_branch);
		mEtJob = (EditText) findViewById(R.id.et_job);
		mRbMale = (RadioButton) findViewById(R.id.rb_male);
		mRbFamale = (RadioButton) findViewById(R.id.rb_famale);

		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_title_save).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_title_save).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_save:
			String name = mEtName.getText().toString().trim();
			String num = mEtNum.getText().toString().trim();
			if (TextUtils.isEmpty(name)) {
				AuxiliaryUtils.toast(this, "姓名不能为空");
				return;
			}

			if (TextUtils.isEmpty(num)) {
				AuxiliaryUtils.toast(this, "电话号码不能为空");
				return;
			}

			if (mIndex == Configs.EDIT_CONTACT) {
				handlerUpdateResult();
			} else {
				handlerAddResult();
			}
			AuxiliaryUtils.hideKeyboard(this, mEtName);
			break;
		case R.id.btn_return:
			AuxiliaryUtils.hideKeyboard(this, mEtName);
			this.finish();
			break;
		case R.id.layout_groups:
			handlerGroupsDialog();
			break;
		case R.id.layout_birthday:
			showDateDialog();
			break;
		}
	}

	private void showDateDialog() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		new DatePickerDialog(this, datePickerDialog, year, monthOfYear,
				dayOfMonth).show();
	}

	DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String m = "";
			String d = "";
			if (monthOfYear < 10) {
				m = "0" + (monthOfYear + 1);
			} else {
				m = (monthOfYear + 1) + "";
			}

			if (dayOfMonth < 10) {
				d = "0" + dayOfMonth;
			} else {
				d = dayOfMonth + "";
			}
			((TextView) findViewById(R.id.et_birthday)).setText(year + "-" + m
					+ "-" + d);
		}
	};

	private String[] res;

	private void handlerGroupsDialog() {
		res = new String[mGroups.size()];
		for (int i = 0; i < mGroups.size(); i++) {
			String name = mGroups.get(i).getName();
			res[i] = name;
		}
		DialogUtil.dialogList(this, "请选择分组", res, "", "", "",
				new DialogOnClickListener() {

					@Override
					public void onDialogClick(DialogInterface dialog,
							int whichButton, int source) {
						((TextView) findViewById(R.id.et_group))
								.setText(res[whichButton]);
					}
				});
	}

	private void handlerUpdateResult() {
		String name = mEtName.getText().toString().trim();
		String tel = mEtNum.getText().toString().trim();
		mContacts.setNAME(name);
		mContacts.setTEL(tel);
		String gname = ((TextView) findViewById(R.id.et_group)).getText()
				.toString().trim();
		mContacts.setGROUPNAME(gname);
		int sex = mRbMale.isChecked() ? 1 : 0;
		String brithday = mEtBirthday.getText().toString().trim();
		String company = mEtCompany.getText().toString().trim();
		String branch = mEtBranch.getText().toString().trim();
		String job = mEtJob.getText().toString().trim();
		Contacts c = new Contacts(mContacts.getContactId() + "", name, tel,
				gname, sex, brithday, company, branch, job);
		mDb.saveFriend(c, mUid);
		mDb.updateMsgForFromJid(tel, name, mContacts.getTEL());
		handlerEditFinish();
		AuxiliaryUtils.toast(ContactDetailActivity.this, "修改联系人成功");
	}

	private void handlerAddResult() {
		String name = mEtName.getText().toString().trim();
		String tel = mEtNum.getText().toString().trim();
		String gname = ((TextView) findViewById(R.id.et_group)).getText()
				.toString().trim();
		int sex = mRbMale.isChecked() ? 1 : 0;
		String brithday = mEtBirthday.getText().toString().trim();
		String company = mEtCompany.getText().toString().trim();
		String branch = mEtBranch.getText().toString().trim();
		String job = mEtJob.getText().toString().trim();
		Contacts c = new Contacts("0", name, tel, gname, sex, brithday,
				company, branch, job);
		mDb.saveFriend(c, mUid);// 联系人存一份，分组存一份
		if (mIndex == Configs.CREATE_CONTACT_BY_STRANGE_NUMBER) {
			mDb.updateMsgForFromJid(tel, name, mContacts.getTEL());
			mDb.delFriend(mContacts);
		}
		handlerCreateFinish();
		AuxiliaryUtils.toast(ContactDetailActivity.this, "新增联系人成功");
	}

	private void handlerCreateFinish() {
		if (friendRefreshHandler != null) {
			friendRefreshHandler.sendEmptyMessage(1);
		}
		ContactDetailActivity.this.finish();
	}

	private void handlerEditFinish() {
		if (friendRefreshHandler != null) {
			friendRefreshHandler.sendEmptyMessage(1);
		}
		ContactDetailActivity.this.finish();
	}
}
