package com.msg.ui;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.TextView;
import com.feihong.msg.sms.R;
import com.msg.bean.ShowMsg;
import com.msg.common.Configs;
import com.msg.widget.ProgressWebView;

public class ChatPreviewActivity2 extends Activity implements OnClickListener {
	private ShowMsg mMsg;
	private TextView mTvTitle;
	private ProgressWebView webview;
	private String webviewpath = "", title ="",msg = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_chat_preview2);
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		mTvTitle = (TextView) findViewById(R.id.tv_content_title);
		webview = (ProgressWebView) findViewById(R.id.webview);
		mTvTitle = ((TextView) findViewById(R.id.tv_card_name));
		Bundle data = getIntent().getExtras();
		if (null != data) {
			mMsg = (ShowMsg) data.get("msg");
			String name = data.getString("name");
			if (!TextUtils.isEmpty(name)) {
				((TextView) findViewById(R.id.tv_title)).setText(name);
			} else {
				((TextView) findViewById(R.id.tv_title)).setText("短信预览");
			}
			if (mMsg != null) {
			    title = mMsg.getTitle();
			    msg = mMsg.getMsg();
				String url = mMsg.getVoiceUrl();
			    webviewpath = mMsg.getMiniPic();

			}
			if (!TextUtils.isEmpty(title)) {
				mTvTitle.setText(title);
			} 
		}
		showWebPage();
	}
	
	private void showWebPage() {
		 webview.getSettings().setJavaScriptEnabled(true);
		 webview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	            @Override
	            public void onFocusChange(View v, boolean hasFocus) {
	                if (hasFocus) {
	                    try {
	                        // 禁止网页上的缩放
	                        Field defaultScale = WebView.class
	                                .getDeclaredField("mDefaultScale");
	                        defaultScale.setAccessible(true);
	                        defaultScale.setFloat(webview, 1.0f);
	                    } catch (SecurityException e) {
	                        e.printStackTrace();
	                    } catch (IllegalArgumentException e) {
	                        e.printStackTrace();
	                    } catch (IllegalAccessException e) {
	                        e.printStackTrace();
	                    } catch (NoSuchFieldException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
		 });
	     webview.setDownloadListener(new DownloadListener() {
	          @Override
	           public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
	               if (url != null && url.startsWith("http://"))
	                   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	           }
	       });

	     if(mMsg != null) {
	    	 if(TextUtils.isEmpty(webviewpath)) {
	    		 webview.loadUrl(mMsg.getMsg());
	    	 } else if(!TextUtils.isEmpty(msg) && msg.contains("http")) {
	    		 webview.loadUrl(mMsg.getMsg());
	    	 } else {
	    		 String tempurl[] = webviewpath.split("\\*");
		    	 webview.loadUrl(Configs.MINICARD_URL + tempurl[0]);
	    	 }
	    	
	 		 
	     }
					
	}
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;

		default:
			break;
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
