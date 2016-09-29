package com.msg.widget;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import com.feihong.msg.sms.R;
import com.msg.common.Configs;
import com.msg.ui.ImApplication;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.NotificationUtils;

public abstract class TabHostActivity extends TabActivity {

	private TabHost mTabHost;
	private TabWidget mTabWidget;
	private LayoutInflater mLayoutflater;
	private ImApplication app;
	private TextView mTvNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set theme because we do not want the shadow
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme(R.style.Theme_Tabhost);
		setContentView(R.layout.api_tab_host);

		mTvNum = (TextView) findViewById(R.id.tab_item_num);

		int left_margin = Configs.screenWidth / 5;
		LayoutParams layout_num = (LayoutParams) mTvNum.getLayoutParams();
		layout_num.leftMargin = left_margin;
		mTvNum.setLayoutParams(layout_num);

		mLayoutflater = getLayoutInflater();

		mTabHost = getTabHost();
		mTabWidget = getTabWidget();
		// mTabWidget.setStripEnabled(false); // need android2.2
		AuxiliaryUtils.addPage(this);
		prepare();

		initTabSpec();
	}

	private void initTabSpec() {

		int count = getTabItemCount();

		for (int i = 0; i < count; i++) {
			// set text view
			View tabItem = mLayoutflater.inflate(R.layout.api_tab_item, null);

			TextView tvTabItem = (TextView) tabItem
					.findViewById(R.id.tab_item_tv);
			tvTabItem.setTextColor(getResources().getColor(R.color.blue));
			setTabItemTextView(tvTabItem, i);
			// set id
			String tabItemId = getTabItemId(i);
			// set tab spec
			TabSpec tabSpec = mTabHost.newTabSpec(tabItemId);
			tabSpec.setIndicator(tabItem);
			tabSpec.setContent(getTabItemIntent(i));

			mTabHost.addTab(tabSpec);
		}
	}

	/** 在初始化界面之前调用 */
	protected void prepare() {
		// do nothing or you override it
	}

	protected int getTabCount() {
		return mTabHost.getTabWidget().getTabCount();
	}

	/** 设置TabItem的图标和标题等 */
	abstract protected void setTabItemTextView(TextView textView, int position);

	abstract protected String getTabItemId(int position);

	abstract protected Intent getTabItemIntent(int position);

	abstract protected int getTabItemCount();

	protected void setCurrentTab(int index) {
		mTabHost.setCurrentTab(index);
	}

	protected void focusCurrentTab(int index) {
		mTabWidget.focusCurrentTab(index);
	}

	@Override
	protected void onResume() {
		super.onResume();
		NotificationUtils.clearNotifi(this);
		app = (ImApplication) getApplication();
		app.setVisibility(true);
	}

	public TextView getmTvNum() {
		return mTvNum;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AuxiliaryUtils.removePage(this);
		app.setVisibility(false);
	}
}
