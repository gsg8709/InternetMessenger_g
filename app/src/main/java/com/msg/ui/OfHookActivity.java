package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.adapter.OfHookSmsAdapter;
import com.msg.bean.SmsRuleResult;
import com.msg.bean.SmsRuleResult.SmsRule;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 挂机短信模板
 * 
 * @author Chris
 * 
 */
public class OfHookActivity extends FragmentActivity implements
		OnClickListener, HttpHandlerListener, OnItemClickListener,
		OnItemLongClickListener, OnScrollListener {
	private ImageView mIbAdd;
	private ListView mLvHookSms;
	private LinearLayout mLayoutProgress, mLayoutFoot;
	private ArrayList<SmsRule> mRules = new ArrayList<SmsRule>();
	private OfHookSmsAdapter mAdapter;
	private ProgressDialog mDialog;
	private int mPage = 1;
	private boolean isLoading = false;
	private SmsRuleResult mSmsRuleResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_ofhook_sms);

		((TextView) findViewById(R.id.tv_title)).setText("挂机短信模板");
		mIbAdd = (ImageView) findViewById(R.id.btn_title_write);
		mIbAdd.setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mIbAdd.setOnClickListener(this);
		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		mLvHookSms = (ListView) findViewById(R.id.lv_hook_sms);
		mLvHookSms.setOnItemClickListener(this);
		mLvHookSms.setOnItemLongClickListener(this);
		mLvHookSms.setOnScrollListener(this);
		mLayoutFoot = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_list_loading_foot, null);
		mLvHookSms.addFooterView(mLayoutFoot);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("删除中，请稍等");
		showDialog();
		handlerCustomData();
	}

	private void handlerCustomData() {
		String uid = UserManager.getUserinfo(this).getUID();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("UID", uid));
		params.add(new BasicNameValuePair("pSize", "10"));
		params.add(new BasicNameValuePair("pageNo", mPage + ""));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.CUSTOM_ONHOOK_LIST_ADDRESS,
				HttpRequestType.POST, params, true,
				NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.btn_title_write:
			Intent intent = new Intent(this, OfHookDetailActivity.class);
			intent.putExtra("index", Configs.CREATE_OFHOOK_SMS_MODE);
			startActivityForResult(intent, 0);
			break;
		}
	}

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLayoutProgress.setVisibility(View.GONE);
				mLvHookSms.setVisibility(View.VISIBLE);

				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}

				handlerListFoot();
			}
		});
	}

	private void handlerListFoot() {
		if (mSmsRuleResult != null) {
			int total = mSmsRuleResult.getTotal();
			if (mPage * 10 >= total) {
				isLoading = true;
				if (total > 0) {
					mLvHookSms.removeFooterView(mLayoutFoot);
				}
			} else {
				isLoading = false;
			}
		}
	}

	private void showDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLayoutProgress.setVisibility(View.VISIBLE);
				mLvHookSms.setVisibility(View.GONE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			if (data != null) {
				Bundle b = data.getExtras();
				SmsRule rule = (SmsRule) b.getSerializable("result");
				mRules.add(0, rule);
				mAdapter.notifyDataSetChanged();
			}
			break;
		case RESULT_FIRST_USER:
			Log.e("Goo", "onActivityResult : RESULT_FIRST_USER");
			isRefresh = true;
			showDialog();
			handlerCustomData();
			break;
		}
	}

	boolean isRefresh = false;

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			final byte[] data, final int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			OfHookActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					switch (mime) {
					case NetHttpHandler.RECEIVE_DATA_MIME_STRING:
						Gson gson = new Gson();
						mSmsRuleResult = gson.fromJson(new String(data),
								SmsRuleResult.class);
						handlerResult(mSmsRuleResult);
						break;
					default:
						if (new String(data).contains("true")) {
							mRules.remove(mIndex);
							mAdapter.notifyDataSetChanged();
						}
						dissmissDialog();
						break;
					}
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			OfHookActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(OfHookActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			OfHookActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(OfHookActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void handlerResult(SmsRuleResult result) {
		if (result.isState()) {
			ArrayList<SmsRule> notes = result.getInfo();
			if (notes != null && notes.size() != 0) {
				handlerList(notes);
			} else {
				AuxiliaryUtils.toast(this, "没有记录");
			}
		} else {
			AuxiliaryUtils.toast(this, "获取数据失败");
		}
		dissmissDialog();
	}

	private void handlerList(ArrayList<SmsRule> ns) {
		if (isRefresh) {
			mRules.clear();
			isRefresh = false;
		}

		mRules.addAll(ns);
		if (mAdapter == null) {
			mAdapter = new OfHookSmsAdapter(this, mRules);
			mLvHookSms.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	private String[] mRes = new String[] { "编辑", "删除" };
	int mIndex = 0;

	private void delSMS(String oid) {
		mDialog.show();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("OID", oid));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.CUSTOM_ONHOOK_DELETE_ADDRESS,
				HttpRequestType.POST, params, true,
				NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		startDetailPage(arg2, Configs.QUERY_OFHOOK_SMS_MODE);
	}

	private void startDetailPage(int arg2, int type) {
		Intent intent = new Intent(OfHookActivity.this,
				OfHookDetailActivity.class);
		intent.putExtra("result", mRules.get(arg2));
		intent.putExtra("index", type);
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int arg2, long arg3) {
		DialogUtil.dialogList(this, "选择操作", mRes, null, null, null,
				new DialogUtil.DialogOnClickListener() {

					@Override
					public void onDialogClick(DialogInterface dialog,
							int whichButton, int source) {
						switch (whichButton) {
						case 0:
							startDetailPage(arg2, Configs.EDIT_OFHOOK_SMS_MODE);
							break;
						case 1:
							mIndex = arg2;
							delSMS(mRules.get(arg2).getOID());
							break;
						}
					}
				});
		return false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount > 0) {
			if (!isLoading) {
				isLoading = true;
				mPage++;
				handlerCustomData();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
