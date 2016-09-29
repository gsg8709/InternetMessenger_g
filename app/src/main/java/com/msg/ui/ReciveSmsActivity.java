package com.msg.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.feihong.msg.sms.R;

public class ReciveSmsActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_register_sms_verify);

		((TextView) findViewById(R.id.tv_title)).setText("短信验证");

		findViewById(R.id.btn_next).setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			startActivity(new Intent(this, UploadHeadActivity.class));
			break;
		case R.id.btn_return:
			this.finish();
			break;
		}
	}
}
