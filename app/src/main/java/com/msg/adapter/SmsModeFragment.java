package com.msg.adapter;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.msg.bean.SmsModeResult;
import com.msg.bean.SmsModeResult.SmsMode;
import com.msg.common.Configs;
import com.msg.ui.ChatSendActivity;
import com.feihong.msg.sms.R;
import com.msg.ui.SmsModeDetailActivity;
import com.msg.utils.AuxiliaryUtils;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

public class SmsModeFragment extends Fragment implements OnScrollListener,
		HttpHandlerListener {
	private LinearLayout mLayoutParent;
	private int mIndex;
	private SmsModeResult mSmsModeResult;
	private LinearLayout mLayoutProgress, mLayoutFoot;
	private ListView mLvNews;
	private int mType = -1;
	private int mPage = 1;
	private boolean isLoading = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayoutParent = (LinearLayout) inflater.inflate(
				R.layout.layout_sms_mode_content, null);
		return mLayoutParent;
	}

	public static SmsModeFragment newInstance(int index, int type) {
		SmsModeFragment fragment = new SmsModeFragment();
		fragment.mIndex = index;
		fragment.mType = type;
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mLvNews = (ListView) getActivity().findViewById(R.id.lv_sms_mode);
		mLayoutProgress = (LinearLayout) getActivity().findViewById(
				R.id.layout_progress);
		mLayoutFoot = (LinearLayout) getActivity().getLayoutInflater().inflate(
				R.layout.layout_list_loading_foot, null);
		mLvNews.setOnScrollListener(this);
		mLvNews.addFooterView(mLayoutFoot);
		mLvNews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SmsMode sm = mSmsModes.get(arg2);
				if (mType == Configs.SEND_FOR_CHOOSE_SMS_MODE) {
					CheckBox cb_sms = (CheckBox) arg1.findViewById(R.id.cb_sms);
					cb_sms.setChecked(true);
					Intent intent = new Intent(getActivity(),
							ChatSendActivity.class);
					intent.putExtra("sm", sm);
					getActivity().setResult(Activity.RESULT_FIRST_USER, intent);
					mType = -1;
					getActivity().finish();
				} else {
					Intent intent = new Intent(getActivity(),
							SmsModeDetailActivity.class);
					intent.putExtra("sm", sm);
					intent.putExtra("type", Configs.QUERY_SMS_MODE);
					startActivityForResult(intent, 0);
				}
			}
		});
		handlerData();
	}

	private void handlerData() {
		isLoading = true;
		NetHttpHandler handler = new NetHttpHandler(getActivity());
		handler.setHttpHandlerListener(this);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pageNo", mPage + ""));
		params.add(new BasicNameValuePair("pSize", "10"));
		params.add(new BasicNameValuePair("CLASSID", (mIndex + 1) + ""));
		handler.execute(Configs.PUBLIC_MODEL_LIST_ADDRESS,
				HttpRequestType.POST, params, true,
				NetHttpHandler.RECEIVE_DATA_MIME_STRING);
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

	private ArrayList<SmsMode> mSmsModes = new ArrayList<SmsMode>();
	private SmsModeAdapter mSmsModeAdapter;

	private void handlerDetailList() {
		if (mSmsModeResult.isState()) {
			ArrayList<SmsMode> news = mSmsModeResult.getInfo();
			if (news != null && news.size() != 0) {
				mSmsModes.addAll(news);
				if (mSmsModeAdapter == null) {
					mSmsModeAdapter = new SmsModeAdapter(getActivity(),
							mSmsModes, mType);
					mLvNews.setAdapter(mSmsModeAdapter);
				} else {
					mSmsModeAdapter.notifyDataSetChanged();
				}
			} else {
				AuxiliaryUtils.toast(getActivity(), "没有记录");
				mLayoutParent.removeAllViews();
			}
		} else {
			AuxiliaryUtils.toast(getActivity(), "获取数据失败");
			mLayoutParent.removeAllViews();
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
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Gson gson_d = new Gson();
					mSmsModeResult = gson_d.fromJson(new String(data),
							SmsModeResult.class);
					handlerDetailList();
					dissmissDialog();
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(getActivity());
				}
			});
			dissmissDialog();
			break;
		default:
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(getActivity(),
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void dissmissDialog() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				isLoading = false;
				mLayoutProgress.setVisibility(View.GONE);
				mLvNews.setVisibility(View.VISIBLE);
				handlerListFoot();
			}
		});
	}

	private void handlerListFoot() {
		if (mSmsModeResult != null) {
			int total = mSmsModeResult.getTotal();
			if (mPage * 10 >= total) {
				isLoading = true;
				if (total > 0) {
					mLayoutFoot.setVisibility(View.GONE);
				}
			} else {
				isLoading = false;
			}
		}
	}

}
