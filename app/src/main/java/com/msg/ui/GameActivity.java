package com.msg.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.feihong.msg.sms.R;

/**
 * 艺智游戏
 * 
 * @author Chris
 * 
 */
public class GameActivity extends Activity implements OnClickListener {
	private WebView mWvGame;
	String mUrl = "http://game.3g.cn";
	private LinearLayout mLayoutProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_games);

		mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
		((TextView) findViewById(R.id.tv_title)).setText("艺智游戏");
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mWvGame = (WebView) findViewById(R.id.wv_game);
		mWvGame.setVerticalScrollBarEnabled(false);
		mWvGame.setHorizontalScrollBarEnabled(false);
		mWvGame.getSettings().setJavaScriptEnabled(false);
		mWvGame.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mWvGame.setVisibility(View.VISIBLE);
				mLayoutProgress.setVisibility(View.GONE);
			}
		});
		mWvGame.loadUrl(mUrl);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWvGame.canGoBack()) {
			mWvGame.goBack();
			return true;
		} else {
			GameActivity.this.finish();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		}
	}
}
