package com.msg.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.feihong.msg.sms.R;

/**
 * 找到
 * 
 * @author Chris
 * 
 */
public class FoundActivity extends Activity implements OnClickListener {
	private LinearLayout mLayoutNews, mLayoutSmsMode, mLayoutMiniCard,
			mLayoutGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_found);

		((TextView) findViewById(R.id.tv_title)).setText("找到");
		mLayoutNews = (LinearLayout) findViewById(R.id.layout_news);
		mLayoutSmsMode = (LinearLayout) findViewById(R.id.layout_sms_mode);
		mLayoutMiniCard = (LinearLayout) findViewById(R.id.layout_mini_card);
		mLayoutGame = (LinearLayout) findViewById(R.id.layout_game);
		mLayoutNews.setOnClickListener(this);
		mLayoutSmsMode.setOnClickListener(this);
		mLayoutMiniCard.setOnClickListener(this);
		mLayoutGame.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_news:
			startActivity(new Intent(this, NewsActivity.class));
			break;
		case R.id.layout_sms_mode:
			startActivity(new Intent(this, SmsModeActivity.class));
			break;
		case R.id.layout_mini_card:
			startActivity(new Intent(this, MiniCardActivity.class));
			break;
		case R.id.layout_game:
			startActivity(new Intent(this, GameActivity.class));
			break;
		}
	}
}
