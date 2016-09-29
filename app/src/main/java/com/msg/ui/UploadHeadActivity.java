package com.msg.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.feihong.msg.sms.R;

public class UploadHeadActivity extends Activity implements OnClickListener {
	private PopupWindow mPopupWindow;
	private LinearLayout mLayoutParent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_register_head);

		((TextView) findViewById(R.id.tv_title)).setText("设置头像");

		findViewById(R.id.iv_head).setOnClickListener(this);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_head_finish).setOnClickListener(this);

		mLayoutParent = (LinearLayout) findViewById(R.id.layout_parent);
		mLayoutParent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hidePop();
				return false;
			}
		});
		LinearLayout layout_button = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.layout_button_dialog, null);
		mPopupWindow = new PopupWindow(layout_button, -1, -2);
		mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style);
	}

	private void showPop() {
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(150);
		mPopupWindow.showAtLocation(mLayoutParent, Gravity.BOTTOM, 0, 0);
	}

	private void hidePop() {
		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(150);
		mPopupWindow.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_head:
			showPop();
			break;
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.btn_head_finish:
			this.finish();
			break;
		}
	}
}
