package com.msg.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.feihong.msg.sms.R;

public class ToolsNewActivity extends Activity implements OnClickListener {

	private ImageView notes_imageview,HookMsg_imageview,Setting_imageview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_tools2);

		((TextView) (findViewById(R.id.tv_title))).setText("工具");
		notes_imageview = (ImageView) findViewById(R.id.notes_imageview);
		HookMsg_imageview = (ImageView) findViewById(R.id.HookMsg_imageview);
		Setting_imageview = (ImageView) findViewById(R.id.Setting_imageview);
		notes_imageview.setOnClickListener(this);
		HookMsg_imageview.setOnClickListener(this);
		Setting_imageview.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.notes_imageview:
			startActivity(new Intent(this, NoteActivity.class));
			break;
		case R.id.HookMsg_imageview:
			startActivity(new Intent(this, OfHookActivity.class));
			break;
		case R.id.Setting_imageview:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		default:
			break;
		}
	}

	
}
