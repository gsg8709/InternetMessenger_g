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
 * 
 * @ClassName: OpenBuessinessByWebActivity 
 * @Description: 采用webview开通业务
 * @author gengsong@zhongsou.com
 * @date 2014年7月11日 下午5:51:03 
 * @version 3.5.2
 */
public class OpenBuessinessByWebActivity extends Activity {

	private ProgressWebView webview;
	private static final String url = "http://www.hulianxs.com/Client/dinggou";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_help);
		initView();
		setClickListener();
	}
	
	private void initView() {
		((TextView) findViewById(R.id.tv_title)).setText(R.string.open_buessiness);
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