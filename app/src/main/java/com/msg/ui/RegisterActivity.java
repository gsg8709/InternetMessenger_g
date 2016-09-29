package com.msg.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.feihong.msg.sms.R;

public class RegisterActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_register);

		((TextView) findViewById(R.id.tv_title)).setText("注册");
		findViewById(R.id.btn_register).setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register:
			startActivity(new Intent(this, ReciveSmsActivity.class));
			break;
		case R.id.btn_return:
			this.finish();
			break;
		}
	}
}
