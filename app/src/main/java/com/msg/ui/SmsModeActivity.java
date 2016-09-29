package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.adapter.SmsModeFragmentAdapter;
import com.msg.bean.SmsModeTypeResult;
import com.msg.bean.SmsModeTypeResult.SmsType;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.widget.TabPageIndicator;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 短信段子
 * 
 * @author Chris
 * 
 */
public class SmsModeActivity extends FragmentActivity implements
		HttpHandlerListener, OnClickListener {
	private LinearLayout mLayoutProgress;
	private ViewPager mLvSmsModes;
	private ArrayList<SmsType> mNewTypes = new ArrayList<SmsType>();
	private TabPageIndicator mIndicator;
	private int mType = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_sms_mode);

		Bundle data = getIntent().getExtras();
		if (null != data) {
			mType = data.getInt("type");
		}

		((TextView) findViewById(R.id.tv_title)).setText("短信段子");
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		mLvSmsModes = (ViewPager) findViewById(R.id.pager);
		mLvSmsModes.setOffscreenPageLimit(0);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		queryTypes();
	}

	private void queryTypes() {
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.PUBLIC_MODEL_TYPE_ADDRESS,
				HttpRequestType.POST, null, true,
				NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN);
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
					case NetHttpHandler.RECEIVE_DATA_MIME_STRING_MAIN:
						Gson gson = new Gson();
						SmsModeTypeResult result = gson.fromJson(new String(
								data), SmsModeTypeResult.class);
						handlerResult(result);
						break;
					}
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(SmsModeActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(SmsModeActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void handlerResult(SmsModeTypeResult result) {
		if (result.isState()) {
			handlerList(result.getInfos());
		}
	}

	private void handlerList(ArrayList<SmsType> infos) {
		if (mNewTypes != null && mNewTypes.size() != 0) {
			mNewTypes.clear();
		}
		mNewTypes.addAll(infos);
		mLvSmsModes.setAdapter(new SmsModeFragmentAdapter(
				getSupportFragmentManager(), mNewTypes, mType));
		mIndicator.setViewPager(mLvSmsModes);
		dissmissDialog();
	}

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLayoutProgress.setVisibility(View.GONE);
				mLvSmsModes.setVisibility(View.VISIBLE);
				mIndicator.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onClick(View v) {
		this.finish();
	}
}
