package com.msg.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.widget.TextView;
import com.feihong.msg.sms.R;
import com.msg.widget.ProgressWebView;
/**
 * 使用帮助
 * @author Administrator
 * @time 2013-3-20
 */
public class HelpActivity extends Activity {

	private ProgressWebView webview;
	private String url = "http://www.hulianxs.com/client/help";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_help);
		initView();
		setClickListener();
	}
	
	private void initView() {
		((TextView) findViewById(R.id.tv_title)).setText("使用帮助");
		 // ~~~ 绑定控件
        webview = (ProgressWebView) findViewById(R.id.webview);
	}
	
	private void setClickListener() {
		 webview.getSettings().setJavaScriptEnabled(true);
	     webview.setDownloadListener(new DownloadListener() {
	          @Override
	           public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
	               if (url != null && url.startsWith("http://"))
	                   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	           }
	       });

	     webview.loadUrl(url);
	}

}
