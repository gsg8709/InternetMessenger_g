package com.msg.adapter;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.bean.MiniCardListResult;
import com.msg.bean.MiniCardListResult.MiniCard;
import com.msg.common.Configs;
import com.msg.ui.ChatSendActivity;
import com.msg.ui.MiniCardActivity;
import com.msg.ui.MiniCardDetailActivity;
import com.msg.utils.AuxiliaryUtils;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

public class MiniCardFragment extends Fragment implements OnScrollListener,
		HttpHandlerListener {
	private LinearLayout mLayoutParent;
	private int mIndex;
	private MiniCardListResult mMiniCardListResult;
	private LinearLayout mLayoutProgress, mLayoutFoot;
	private ListView mLvCards;
	private int mPage = 1;
	private boolean isLoading = false;
	private int mFromSms = -1;
	private ArrayList<MiniCard> mMiniCards = new ArrayList<MiniCard>();
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayoutParent = (LinearLayout) inflater.inflate(
				R.layout.layout_news_content, null);
		return mLayoutParent;
	}

	public static MiniCardFragment newInstance(int index, int type) {
		MiniCardFragment fragment = new MiniCardFragment();
		fragment.mIndex = index;
		fragment.mFromSms = type;
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mLvCards = (ListView) getActivity().findViewById(R.id.lv_news);
		mLayoutProgress = (LinearLayout) getActivity().findViewById(
				R.id.layout_progress);
		mLayoutFoot = (LinearLayout) getActivity().getLayoutInflater().inflate(
				R.layout.layout_list_loading_foot, null);
		mLvCards.setOnScrollListener(this);
		mLvCards.addFooterView(mLayoutFoot);
		mLvCards.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mFromSms == Configs.SEND_FOR_CHOOSE_MINI_CARD) {
					Intent intent = new Intent(getActivity(),
							ChatSendActivity.class);
					intent.putExtra("card", mMiniCards.get(arg2));
					getActivity().setResult(Activity.RESULT_FIRST_USER, intent);
					mFromSms = -1;
					getActivity().finish();
				} else {
					Intent intent = new Intent(getActivity(),
							MiniCardDetailActivity.class);
					intent.putExtra("card", mMiniCards.get(arg2));
					intent.putExtra("id", mMiniCards.get(arg2).getNID());
					getActivity().startActivity(intent);
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
		params.add(new BasicNameValuePair("CLASSID", (mIndex) + ""));
		handler.execute(Configs.MINI_CARD_LIST_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
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

	private MiniCardAdapter mMiniCardAdapter;

	private void handlerDetailList() {
		if (mMiniCardListResult.isState()) {
			ArrayList<MiniCard> news = mMiniCardListResult.getInfos();
			if (news != null && news.size() != 0) {
				if(mMiniCards != null) {
					mMiniCards.clear();
				}
				mMiniCards.addAll(news);
				if (mMiniCardAdapter == null) {
					mMiniCardAdapter = new MiniCardAdapter(getActivity(),
							mMiniCards);
					mLvCards.setAdapter(mMiniCardAdapter);
					MiniCardActivity.mIndicator.notifyDataSetChanged();
				} else {
					mMiniCardAdapter.notifyDataSetChanged();
					MiniCardActivity.mIndicator.notifyDataSetChanged();
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
					mMiniCardListResult = gson_d.fromJson(new String(data),
							MiniCardListResult.class);
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
				mLvCards.setVisibility(View.VISIBLE);
				handlerListFoot();
			}
		});
	}

	private void handlerListFoot() {
		if (mMiniCardListResult != null) {
			int total = mMiniCardListResult.getTotal();
			if (mPage * 10 >= total) {
				isLoading = true;
				if (total > 0) {
					mLayoutFoot.setVisibility(View.GONE);
				}
			} else {
				isLoading = false;
				mLayoutProgress.setVisibility(View.GONE);
				
				int isVisibel = mLayoutProgress.getVisibility();
				
//				if (mMiniCardAdapter == null) {
//					mMiniCardAdapter = new MiniCardAdapter(getActivity(),
//							mMiniCards);
//					mLvCards.setAdapter(mMiniCardAdapter);
//				} else {
//					mMiniCardAdapter = new MiniCardAdapter(getActivity(),
//							mMiniCards);
//					mLvCards.setAdapter(mMiniCardAdapter);
//					mMiniCardAdapter.notifyDataSetChanged();
//					mLayoutProgress.setVisibility(View.GONE);
//					mLvCards.setVisibility(View.VISIBLE);
//				}
			}
		}
		
		
	}

}
