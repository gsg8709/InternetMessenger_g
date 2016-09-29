package com.msg.ui;

import java.net.HttpURLConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.bean.Result;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 欢迎页
 * 
 * @author Chris
 * 
 */
@SuppressLint("HandlerLeak")
public class GuideActivity extends Activity implements HttpHandlerListener {
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		layout.setBackgroundResource(R.drawable.guide);
		setContentView(layout);
		tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		getWindowParam();
		int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		flags |= WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
		flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		getWindow().addFlags(flags);
		if (UserManager.getUserinfo(GuideActivity.this) != null
				&& !UserManager.getLogout(GuideActivity.this)) {
			startActivity(new Intent(GuideActivity.this, MainActivity.class));
			GuideActivity.this.finish();
		} else {
			if (Configs.verification) {
				getBackDoor();
			} else {
				mHandler.sendEmptyMessageDelayed(0, 3000);
			}
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			startActivity();
		}
	};

	public void getBackDoor() {
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.PUBLIC_MODEL_TEST_ADDRESS,
				HttpRequestType.POST, null, true,
				NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	@SuppressWarnings("deprecation")
	private void getWindowParam() {
		Configs.imei = AuxiliaryUtils.getIMEI(tm);
		Configs.device = android.os.Build.MODEL;
		Configs.sysVersion = android.os.Build.VERSION.RELEASE;

		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		Configs.screenWidth = display.getWidth();
		Configs.screenHeight = display.getHeight();
		Configs.scale = this.getResources().getDisplayMetrics().density;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Configs.screenDensity = dm.density;
	}

	private void startActivity() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(GuideActivity.this,
						LoginActivity.class));
				GuideActivity.this.finish();
			}
		});
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			byte[] data, int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			Gson gson = new Gson();
			Result result = gson.fromJson(new String(data), Result.class);
			if (result.isState()) {
				mHandler.sendEmptyMessageDelayed(0, 1500);
			}
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			mHandler.sendEmptyMessageDelayed(0, 1500);
			break;
		default:
			mHandler.sendEmptyMessageDelayed(0, 1500);
			break;
		}
	}
}
