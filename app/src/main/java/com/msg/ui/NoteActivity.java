package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.adapter.NoteAdapter;
import com.msg.bean.NoteResult;
import com.msg.bean.NoteResult.Note;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 人脉记事列表
 * 
 * @author Chris
 * 
 */
public class NoteActivity extends Activity implements OnClickListener,
		HttpHandlerListener, OnItemClickListener, OnItemLongClickListener,
		OnScrollListener {
	private ListView mLvNote, mLvSearchNote;
	private LinearLayout mLayoutSearch, mLayoutSearchProgress, mLayoutContent,
			mLayoutProgress, mLayoutFoot;
	private ArrayList<Note> mNotes = new ArrayList<NoteResult.Note>();
	private NoteAdapter mAdapter;

	private RelativeLayout mLayoutSearchShow;
	/**
	 * 搜索框
	 */
	private EditText mEtSearch;
	private ImageView mImgCover;
	private TextView mTvCancel;
	private ProgressDialog mDialog;
	private NoteResult mNoteResult;
	private int mPage = 1;
	private boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_note);
		((TextView) findViewById(R.id.tv_title)).setText("人脉记事");
		findViewById(R.id.btn_title_write).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_title_write).setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mLayoutSearchProgress = (LinearLayout) findViewById(R.id.layout_search_progress);
		mLvNote = (ListView) findViewById(R.id.lv_note);
		mLvNote.setOnItemClickListener(this);
		mLvNote.setOnItemLongClickListener(this);
		mLayoutFoot = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_list_loading_foot, null);
		mLvNote.addFooterView(mLayoutFoot);
		mLvNote.setOnScrollListener(this);
		mLayoutContent = (LinearLayout) findViewById(R.id.layout_content);
		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		mLayoutSearch = (LinearLayout) findViewById(R.id.layout_search_show);
		mLayoutSearch.setOnClickListener(this);

		mTvCancel = (TextView) findViewById(R.id.btn_searchbar_cancel);
		mTvCancel.setOnClickListener(this);
		mLvSearchNote = (ListView) findViewById(R.id.list_search_note);
		mImgCover = (ImageView) findViewById(R.id.img_cover);
		mLayoutSearchShow = (RelativeLayout) findViewById(R.id.layout_note_search);
		mEtSearch = (EditText) findViewById(R.id.searchbar_input_text);
		mEtSearch.setHint("搜索记事");
		mEtSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				searchKeyword();
				return false;
			}
		});
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("删除中，请稍等");
		showDialog();
		handlerData();
	}

	/**
	 * 联系人搜索
	 */
	private void searchKeyword() {
		mLayoutSearchProgress.setVisibility(View.VISIBLE);
		String keyword = mEtSearch.getText().toString().trim();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("WORD", keyword));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.WORDPAD_LIST_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	private void handlerData() {
		isLoading = true;
		String uid = UserManager.getUserinfo(this).getUID();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("UID", uid));
		params.add(new BasicNameValuePair("pSize", "10"));
		params.add(new BasicNameValuePair("pageNo", mPage + ""));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.WORDPAD_LIST_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_search_show:// 打开搜索
			// searchAnimation(true);
			startActivity(new Intent(this, NoteSearchActivity.class));
			break;
		case R.id.btn_searchbar_cancel:// 关闭搜索
			searchAnimation(false);
			break;
		case R.id.btn_title_write:
			Intent intent = new Intent(this, NoteAddActivity.class);
			intent.putExtra("type", Configs.CREATE_NOTE);
			startActivityForResult(intent, 0);
			break;
		case R.id.btn_return:
			this.finish();
			break;
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
			mLayoutContent.startAnimation(translateAnimation);

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
					AuxiliaryUtils.autoPopupSoftInput(NoteActivity.this,
							mEtSearch);
					mLayoutContent.clearAnimation();
					mLayoutContent.setVisibility(View.GONE);
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
			mLayoutContent.clearAnimation();
			mEtSearch.setText("");
			mLvSearchNote.setAdapter(null);
			mTvCancel.setVisibility(View.GONE);
			mLayoutContent.setVisibility(View.VISIBLE);
			mLayoutSearchShow.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			isRefrehs = true;
			showDialog();
			handlerData();
			break;
		case RESULT_FIRST_USER:
			if (data != null) {
				isRefrehs = true;
				Bundle b = data.getExtras();
				int index = b.getInt("index");
				mNotes.remove(index);
				mAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	boolean isRefrehs = false;

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
					if (mime == NetHttpHandler.RECEIVE_DATA_MIME_STRING_DEL_ONE_MSG) {
						if (new String(data).contains("true")) {
							mNotes.remove(mIndex);
							mAdapter.notifyDataSetChanged();
						} else {
							AuxiliaryUtils.toast(NoteActivity.this, "删除失败");
						}
						dissmissDialog(mime);
					} else {
						Gson gson = new Gson();
						mNoteResult = gson.fromJson(new String(data),
								NoteResult.class);
						handlerResult(mNoteResult, mime);
					}
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(NoteActivity.this);
				}
			});
			dissmissDialog(mime);
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(NoteActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog(mime);
			break;
		}
	}

	/**
	 * 处理通讯录列表数据显示刷新
	 * 
	 * @param cgs
	 */
	private void handlerList(ArrayList<Note> ns) {
		if (isRefrehs) {
			mNotes.clear();
		}
		
		mNotes.addAll(ns);
		refreshListView();
	}

	private void refreshListView() {
		if (mAdapter == null) {
			mAdapter = new NoteAdapter(NoteActivity.this, mNotes);
			mLvNote.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		mLvNote.setVisibility(View.VISIBLE);
	}

	private void handlerResult(NoteResult result, int mime) {
		if (result.isState()) {
			ArrayList<Note> notes = result.getInfo();
			if (notes != null && notes.size() != 0) {
				if (mime == NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN) {
					handlerList(notes);
				} else {
					handlerSearchList(notes);
				}
			} else {
				AuxiliaryUtils.toast(NoteActivity.this, "没有人脉记事");
			}
		} else {
			AuxiliaryUtils.toast(NoteActivity.this, "获取数据失败");
		}
		dissmissDialog(mime);
	}

	private void handlerSearchList(final ArrayList<Note> notes) {
		NoteAdapter adapter = new NoteAdapter(NoteActivity.this, notes);
		mLvSearchNote.setAdapter(adapter);
		mLvSearchNote.setVisibility(View.VISIBLE);
		mLvSearchNote.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(NoteActivity.this,
						NoteDetailActivity.class);
				intent.putExtra("note", mNotes.get(arg2));
				intent.putExtra("index", arg2);
				startActivityForResult(intent, 0);
				searchAnimation(false);
			}
		});
	}

	private void dissmissDialog(final int mime) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLayoutSearch.setVisibility(View.VISIBLE);
				mLayoutProgress.setVisibility(View.GONE);
				mLvNote.setVisibility(View.VISIBLE);
				if (mime == NetHttpHandler.RECEIVE_DATA_MIME_STRING) {
					mLayoutSearchProgress.setVisibility(View.GONE);
					mImgCover.clearAnimation();
					mImgCover.setVisibility(View.GONE);
				} else {
					handlerListFoot();
				}

				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		});
	}

	private void handlerListFoot() {
		if (mNoteResult != null) {
			int total = mNoteResult.getTotal();
			if (mPage * 10 >= total && total != 0) {
				isLoading = true;
				if (total > 0) {
					mLvNote.removeFooterView(mLayoutFoot);
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
				mLayoutSearch.setVisibility(View.GONE);
				mLayoutProgress.setVisibility(View.VISIBLE);
				mLvNote.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, NoteDetailActivity.class);
		intent.putExtra("note", mNotes.get(arg2));
		intent.putExtra("index", arg2);
		startActivityForResult(intent, 0);
	}

	private String[] mRes = new String[] { "编辑", "删除" };
	int mIndex = 0;

	private void delNote(Integer oid) {
		mDialog.show();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("WID", "" + oid));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.WORDPAD_DELETE_ADDRESS, HttpRequestType.POST,
				params, true,
				NetHttpHandler.RECEIVE_DATA_MIME_STRING_DEL_ONE_MSG);
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
							Intent intent = new Intent(NoteActivity.this,
									NoteAddActivity.class);
							intent.putExtra("data", mNotes.get(arg2));
							intent.putExtra("type", Configs.EDIT_NOTE);
							startActivityForResult(intent, 0);
							break;
						case 1:
							mIndex = arg2;
							delNote(mNotes.get(arg2).getWID());
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
				mPage++;
				handlerData();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
