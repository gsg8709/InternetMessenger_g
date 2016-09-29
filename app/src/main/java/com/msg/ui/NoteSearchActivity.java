package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.adapter.NoteAdapter;
import com.msg.bean.Contacts;
import com.msg.bean.NoteResult;
import com.msg.bean.NoteResult.Note;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.TimeRender;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

public class NoteSearchActivity extends Activity implements OnClickListener,
		HttpHandlerListener {
	private boolean isStart = false;
	private TextView mTvStart, mTvEnd;
	private EditText mTvContacts;
	private ArrayList<Contacts> mListContacts = new ArrayList<Contacts>();
	private ArrayList<Note> mNotes = new ArrayList<NoteResult.Note>();
	private String mStartPageTime;
	private ProgressDialog mDialog;
	private ListView mLvSearchNote;
	private NoteAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_note);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("搜索中，请稍等");
		mLvSearchNote = (ListView) findViewById(R.id.list_search_note);
		((TextView) findViewById(R.id.tv_title)).setText("搜索人脉记事");
		findViewById(R.id.layout_time_start).setOnClickListener(this);
		findViewById(R.id.layout_time_end).setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_title_finish).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_title_finish).setOnClickListener(this);
		findViewById(R.id.btn_choose).setOnClickListener(this);
		mTvContacts = (EditText) findViewById(R.id.tv_contacts);
		((Button) findViewById(R.id.btn_title_finish)).setText("搜索");
		mTvStart = (TextView) findViewById(R.id.tv_time_start);
		mTvEnd = (TextView) findViewById(R.id.tv_time_end);
		mStartPageTime = TimeRender.getDate();
		mTvStart.setText("起始 " + mStartPageTime);
		mTvEnd.setText("截止 " + mStartPageTime);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.btn_title_finish:
			searchNote();
			break;
		case R.id.layout_time_start:
			isStart = true;
			showDateDialog();
			break;
		case R.id.layout_time_end:
			isStart = false;
			showDateDialog();
			break;
		case R.id.btn_choose:
			Intent intent = new Intent(this, ContactsChooseActivity.class);
			intent.putExtra("page", Configs.FROM_NOTE_PAGE);
			startActivityForResult(intent, 0);
			break;
		}
	}

	private void searchNote() {
		mDialog.show();
		String start_time = mTvStart.getText().toString().trim();
		String end_time = mTvEnd.getText().toString().trim();
		long start = TimeRender.stringToLong(
				start_time.substring(3, start_time.length()), "yyyy-MM-dd");
		long end = TimeRender.stringToLong(
				end_time.substring(3, end_time.length()), "yyyy-MM-dd");
		start_time = (start + "").substring(0, (start + "").length() - 3);
		end_time = (end + "").substring(0, (end + "").length() - 3);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("UID", UserManager.getUserinfo(this)
				.getUID()));
		if (mListContacts.size() != 0) {
			Contacts c = mListContacts.get(0);
			params.add(new BasicNameValuePair("CID", c.getContactId() + ""));
		}
		params.add(new BasicNameValuePair("EDITBEGINTIME", start_time));
		params.add(new BasicNameValuePair("EDITENDTIME", end_time));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.WORDPAD_LIST_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
		System.out.println(Configs.WORDPAD_LIST_ADDRESS + params.toString());
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
			if (monthOfYear < 9) {
				m = "0" + (monthOfYear + 1);
			} else {
				m = (monthOfYear + 1) + "";
			}
			if (dayOfMonth < 10) {
				d = "0" + dayOfMonth;
			} else {
				d = dayOfMonth + "";
			}
			if (isStart) {
				mTvStart.setText("起始 " + year + "-" + m + "-" + d);
			} else {
				mTvEnd.setText("截止 " + year + "-" + m + "-" + d);
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_FIRST_USER:
			Bundle b = data.getExtras();
			mListContacts = (ArrayList<Contacts>) b.getSerializable("result");
			if (mListContacts.size() != 0) {
				mTvContacts.setText(mListContacts.get(0).getNAME());
			}
			break;
		}
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
					Gson gson = new Gson();
					NoteResult result = gson.fromJson(new String(data),
							NoteResult.class);
					handlerResult(result);
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(NoteSearchActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(NoteSearchActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	/**
	 * 处理通讯录列表数据显示刷新
	 * 
	 * @param cgs
	 */
	private void handlerList(ArrayList<Note> ns) {
		if (mNotes != null && mNotes.size() != 0) {
			mNotes.clear();
		}
		mNotes.addAll(ns);
		refreshListView();
	}

	private void refreshListView() {
		if (mAdapter == null) {
			mAdapter = new NoteAdapter(NoteSearchActivity.this, mNotes);
			mLvSearchNote.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void handlerResult(NoteResult result) {
		if (result.isState()) {
			final ArrayList<Note> notes = result.getInfo();

			handlerList(notes);
			mLvSearchNote.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent intent = new Intent(NoteSearchActivity.this,
							NoteDetailActivity.class);
					intent.putExtra("note", notes.get(arg2));
					intent.putExtra("index", arg2);
					startActivityForResult(intent, 0);
				}
			});

			if (notes.size() == 0) {
				AuxiliaryUtils.toast(NoteSearchActivity.this, "没有人脉记事");
			}
		} else {
			AuxiliaryUtils.toast(NoteSearchActivity.this, "获取数据失败");
		}
		dissmissDialog();
	}

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
	}

}
