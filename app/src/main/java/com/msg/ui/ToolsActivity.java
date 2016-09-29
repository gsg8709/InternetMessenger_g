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
 * 工具
 * 
 * @author Chris
 * 
 */
public class ToolsActivity extends Activity implements OnClickListener {
	private LinearLayout mLayoutNotes, mLayoutHookMsg, mlayoutSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_tools);

		((TextView) (findViewById(R.id.tv_title))).setText("工具");
		mLayoutNotes = (LinearLayout) findViewById(R.id.layout_contacts_notes);
		mLayoutHookMsg = (LinearLayout) findViewById(R.id.layout_hook_msg);
		mlayoutSetting = (LinearLayout) findViewById(R.id.layout_setting);
		mLayoutNotes.setOnClickListener(this);
		mLayoutHookMsg.setOnClickListener(this);
		mlayoutSetting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_contacts_notes:
			startActivity(new Intent(this, NoteActivity.class));
			break;
		case R.id.layout_hook_msg:
			startActivity(new Intent(this, OfHookActivity.class));
			break;
		case R.id.layout_setting:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		}
	}
}
