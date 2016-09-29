package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.adapter.NewsAdapter;
import com.msg.bean.NewsListResult;
import com.msg.bean.NewsListResult.News;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;
/**
 * 
 * @function 有图有料列表
 * @author gengsong
 * 2014-5-5下午9:49:49
 */
public class NewsActivity extends Activity implements HttpHandlerListener,
		OnClickListener, OnScrollListener {
	private LinearLayout mLayoutProgress;
	private ListView mLvNews;
	private ArrayList<News> mNews = new ArrayList<News>();
	private NewsAdapter mNewsAdapter;
	private int mFromSms = -1;
	private int mPage = 1;
	private NewsListResult result_d;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sms_content);

		Bundle data = getIntent().getExtras();
		if (null != data) {
			mFromSms = data.getInt("type");
		}

		((TextView) findViewById(R.id.tv_title)).setText("有图有料");
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		mLvNews = (ListView) findViewById(R.id.lv_news);
		layout_foot = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_list_loading_foot, null);
		mLvNews.addFooterView(layout_foot);
		mLvNews.setOnScrollListener(this);
		handlerData();
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
					switch (mime) {
					case NetHttpHandler.RECEIVE_DATA_MIME_STRING:
						Gson gson_d = new Gson();
						result_d = gson_d.fromJson(new String(data),
								NewsListResult.class);
						handlerPageResult(result_d);
						break;
					}
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(NewsActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(NewsActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void handlerPageResult(NewsListResult result) {
		mLvNews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mFromSms == Configs.SEND_FOR_CHOOSE_NEWS) {
					Intent intent = new Intent(NewsActivity.this,
							ChatSendActivity.class);
					intent.putExtra("news", mNews.get(arg2));
					NewsActivity.this.setResult(Activity.RESULT_FIRST_USER,
							intent);
					mFromSms = -1;
					NewsActivity.this.finish();
				} else {
					News news = mNews.get(arg2);
					Intent intent = new Intent(NewsActivity.this,
							NewDetailActivity.class);
					intent.putExtra("news", news);
					NewsActivity.this.startActivity(intent);
				}
			}
		});

		if (result.isState()) {
			ArrayList<News> news = result.getInfos();
			if (news != null && news.size() != 0) {
				handlerDetailList(mLvNews, news);
			} else {
				AuxiliaryUtils.toast(this, "没有记录");
			}
		} else {
			AuxiliaryUtils.toast(this, "获取数据失败");
		}
		mLayoutProgress.setVisibility(View.GONE);
		mLvNews.setVisibility(View.VISIBLE);
		dissmissDialog();
	}

	private void handlerDetailList(ListView lv, ArrayList<News> ns) {
		mNews.addAll(ns);
		if (mNewsAdapter == null) {
			mNewsAdapter = new NewsAdapter(this, mNews);
			lv.setAdapter(mNewsAdapter);
		} else {
			mNewsAdapter.notifyDataSetChanged();
		}
	}

	private void handlerData() {
		isLoading = true;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pageNo", mPage + ""));
		params.add(new BasicNameValuePair("pSize", 10 + ""));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.NEWS_LIST_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	private LinearLayout layout_foot;

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLayoutProgress.setVisibility(View.GONE);
				mLvNews.setVisibility(View.VISIBLE);
				isLoading = false;
				handlerListFoot();
			}
		});
	}

	private void handlerListFoot() {
		if (result_d != null) {
			int total = result_d.getTotal();
			if (mPage * 10 > total) {
				isLoading = true;
				if (total > 0) {
					mLvNews.removeFooterView(layout_foot);
				}
			} else {
				isLoading = false;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		}
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

	boolean isLoading = false;
}
