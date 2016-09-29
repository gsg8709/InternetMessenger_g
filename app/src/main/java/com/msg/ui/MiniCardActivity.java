package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.adapter.MiniCardAdapter;
import com.msg.adapter.MiniCardFragmentAdapter;
import com.msg.bean.MiniCardListResult;
import com.msg.bean.MiniCardListResult.MiniCard;
import com.msg.bean.MiniCardTypeResult;
import com.msg.bean.MiniCardTypeResult.MiniCardType;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.widget.TabPageIndicator;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 迷你卡片页面
 * 
 * @author gongchao
 * 
 */
public class MiniCardActivity extends FragmentActivity implements
		OnClickListener, HttpHandlerListener {
	private LinearLayout mLayoutProgress;
	private ViewPager mLvCards;
	private ArrayList<MiniCardType> mMiniCardTypes = new ArrayList<MiniCardType>();
	public static TabPageIndicator mIndicator;
	private int mFromSms = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mini_card);

		Bundle data = getIntent().getExtras();
		if (null != data) {
			mFromSms = data.getInt("type");
		}

		((TextView) findViewById(R.id.tv_title)).setText("迷你卡片");
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		mLvCards = (ViewPager) findViewById(R.id.pager);
		mLvCards.setOffscreenPageLimit(0);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		queryTypes();
	}

	private void queryTypes() {
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.MINI_CARD_TYPE_ADDRESS, HttpRequestType.POST,
				null, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN);
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
					MiniCardTypeResult result = gson.fromJson(new String(data),
							MiniCardTypeResult.class);
					handlerResult(result);
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(MiniCardActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(MiniCardActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLayoutProgress.setVisibility(View.GONE);
				mLvCards.setVisibility(View.VISIBLE);
				mIndicator.setVisibility(View.VISIBLE);
			}
		});
	}

	private void handlerResult(MiniCardTypeResult result) {
		if (result.isState()) {
			handlerList(result.getInfos());
		}
	}

	private void handlerList(ArrayList<MiniCardType> infos) {
		if (mMiniCardTypes != null && mMiniCardTypes.size() != 0) {
			mMiniCardTypes.clear();
		}
		mMiniCardTypes.addAll(infos);
		mLvCards.setAdapter(new MiniCardFragmentAdapter(
				getSupportFragmentManager(), mMiniCardTypes, mFromSms));
		mIndicator.setViewPager(mLvCards,0);
		dissmissDialog();
	}
}
