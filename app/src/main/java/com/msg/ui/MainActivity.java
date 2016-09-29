package com.msg.ui;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.feihong.msg.sms.R;
import com.msg.server.RetrievalSmsService;
import com.msg.ui.InterconnectionSmsActivity.onRefrehsMsgListener;
import com.msg.widget.TabHostActivity;
import com.msg.widget.TabItem;

public class MainActivity extends TabHostActivity implements
		onRefrehsMsgListener {

	List<TabItem> mItems;
	private String pw;
	private String id;

	/**
	 * 在初始化TabWidget前调用 和TabWidget有关的必须在这里初始化
	 */
	@Override
	protected void prepare() {
		Intent intent = MainActivity.this.getIntent();
		pw = intent.getStringExtra("pw");
		id = intent.getStringExtra("id");
		intent = new Intent().setClass(this, InterconnectionSmsActivity.class);
		intent.putExtra("pw", pw);
    	intent.putExtra("id", id);
		TabItem home = new TabItem("互联短信", // title
				R.drawable.tab_msg, // icon
				R.drawable.maintab_toolbar_bg, // background
				new Intent(this, InterconnectionSmsActivity.class)); // intent

		TabItem info = new TabItem("人脉圈", R.drawable.tab_selfinfo,
				R.drawable.maintab_toolbar_bg, new Intent(this,
						ContactsActivity.class));

		TabItem msg = new TabItem("找到", R.drawable.tab_home,
				R.drawable.maintab_toolbar_bg, new Intent(this,
						FoundActivity.class));

		TabItem square = new TabItem("工具", R.drawable.tab_square,
				R.drawable.maintab_toolbar_bg, new Intent(this,
						ToolsNewActivity.class));

		mItems = new ArrayList<TabItem>();
		mItems.add(home);
		mItems.add(info);
		mItems.add(msg);
		mItems.add(square);
	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, RetrievalSmsService.class));
		setCurrentTab(0);
		InterconnectionSmsActivity.INSTANCE.setonRefrehsMsgListener(this);
	
	}

	/** tab的title，icon，边距设定等等  **/
	@Override
	protected void setTabItemTextView(TextView textView, int position) {
		textView.setPadding(0, 6, 0, 3);
		textView.setText(mItems.get(position).getTitle());
		textView.setTextColor(this.getResources().getColor(R.color.blue));
		textView.setBackgroundResource(mItems.get(position).getBg());
		textView.setCompoundDrawablesWithIntrinsicBounds(0, mItems
				.get(position).getIcon(), 0, 0);

	}

	/** tab唯一的id */
	@Override
	protected String getTabItemId(int position) {
		return mItems.get(position).getTitle(); // 我们使用title来作为id，你也可以自定
	}

	/** 点击tab时触发的事件 */
	@Override
	protected Intent getTabItemIntent(int position) {
		return mItems.get(position).getIntent();
	}

	@Override
	protected int getTabItemCount() {
		return mItems.size();
	}

	@Override
	public void onRefresh(int num) {
		if (num > 0) {
			getmTvNum().setVisibility(View.VISIBLE);
			getmTvNum().setText("" + num);
		} else {
			getmTvNum().setVisibility(View.GONE);
		}
	}
	
	
}
