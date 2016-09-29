package com.msg.ui;

import com.feihong.msg.sms.R;
import com.msg.utils.DialogUtil;
import com.msg.utils.SendMessage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 开通业务
 * @author gengsong
 * @time 2014-3-18
 */
public class OpenBuessinessActivity extends Activity implements OnClickListener {

	private LinearLayout layout_01,layout_02,layout_03,layout_04,layout_05,layout_06;
	private SendMessage mSendMessage;
	private static final String mComTel_num = "10658565";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_openbus);
		initView();
		setClickListener();
		mSendMessage = new SendMessage(this);
	}
	
	private void initView() {
		((TextView) findViewById(R.id.tv_title)).setText("订购");
		
		layout_01 = (LinearLayout) this.findViewById(R.id.layout_01);
		layout_02 = (LinearLayout) this.findViewById(R.id.layout_02);
		layout_03 = (LinearLayout) this.findViewById(R.id.layout_03);
		layout_04 = (LinearLayout) this.findViewById(R.id.layout_04);
		layout_05 = (LinearLayout) this.findViewById(R.id.layout_05);
		layout_06 = (LinearLayout) this.findViewById(R.id.layout_06);
	}
	
	private void setClickListener() {
		layout_01.setOnClickListener(this);
		layout_02.setOnClickListener(this);
		layout_03.setOnClickListener(this);
		layout_04.setOnClickListener(this);
		layout_05.setOnClickListener(this);
		layout_06.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_01://5元包月
			mSendMessage.send(mComTel_num, "wyby");
			DialogUtil.dialogMessage(this, "提示", "您将定制5元会员专享包月业务，稍后系统会下发确认短信，可按提示回复“是”，成功后，即可享受会员专享服务。感谢您的使用。", null, null, null, null);
			break;
		case R.id.layout_02://10元包月
			mSendMessage.send(mComTel_num, "syby");	
			DialogUtil.dialogMessage(this, "提示", "您将定制10元会员专享包月业务，稍后系统会下发确认短信，可按提示回复“是”，成功后，即可享受会员专享服务。感谢您的使用。", null, null, null, null);
			break;
		case R.id.layout_03://1元点播
			mSendMessage.send(mComTel_num, "yydb");	
			DialogUtil.dialogMessage(this, "提示", "您已成功点播1元增量包服务，当月可免费发送30条互联短信，次月自动失效。感谢您的使用。", null, null, null, null);
			break;
		case R.id.layout_04://2元点播
			mSendMessage.send(mComTel_num, "eydb");	
			DialogUtil.dialogMessage(this, "提示", "您已成功点播2元增量包服务，当月可免费发送60条互联短信，次月自动失效。感谢您的使用。", null, null, null, null);
			break;
		case R.id.layout_05://5元点播
			mSendMessage.send(mComTel_num, "wydb");	
			DialogUtil.dialogMessage(this, "提示", "您已成功点播5元增量包服务，当月可免费发送150条互联短信，次月自动失效。感谢您的使用。", null, null, null, null);
			break;
		case R.id.layout_06://10元点播
			mSendMessage.send(mComTel_num, "sydb");	
			DialogUtil.dialogMessage(this, "提示", "您已成功点播10元增量包服务，当月可免费发送300条互联短信，次月自动失效。感谢您的使用。", null, null, null, null);
			break;

		default:
			break;
		}
	}
	
}
