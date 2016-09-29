package com.msg.ui;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feihong.msg.sms.R;
import com.msg.bean.NewsListResult.News;
import com.msg.common.Configs;
import com.msg.utils.AudioRecord;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.IOUtil;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.widget.ChatTextPopDialog;

/**
 * 新鲜事详情
 * 
 * @author gongchao
 * 
 */
@SuppressLint("HandlerLeak")
public class NewDetailActivity extends Activity implements OnClickListener {
	private News mNews;
	private TextView mTvTitle;
	private TextView mTvContent;
	private ImageView mImg, mIvPlay;
	private Bitmap mBitmap;
	private ImageBitmapCache ibc;
	private AudioRecord mRecord;
	private RelativeLayout mLayoutRecord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_news_detail);

		mLayoutRecord = (RelativeLayout) findViewById(R.id.layout_record);
		((TextView) (findViewById(R.id.tv_title))).setText("有图有料");
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.layout_detail_input).setVisibility(View.GONE);
		mTvTitle = (TextView) findViewById(R.id.tv_card_name);
		mTvContent = (TextView) findViewById(R.id.tv_card_content);
		mImg = (ImageView) findViewById(R.id.iv_img);
		mImg.setOnClickListener(this);
		ibc = ImageBitmapCache.getInstance();
		mIvPlay = (ImageView) findViewById(R.id.iv_play);
		mIvPlay.setOnClickListener(this);
		Bundle data = getIntent().getExtras();
		mRecord = new AudioRecord();
		if (null != data) {
			mNews = (News) data.get("news");
			if (mNews != null) {
				String title = mNews.getTITLE();
				mTvContent.setText(mNews.getMSG());
				handlerImg(mNews.getIMAGE());
				String url = mNews.getAUDIO();
				if (!TextUtils.isEmpty(url)) {
					mIvPlay.setVisibility(View.VISIBLE);
				}

				if (!TextUtils.isEmpty(title)) {
					mTvTitle.setText(title);
				} else if (TextUtils.isEmpty(url)) {
					mLayoutRecord.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecord.stopPlaying();
	}

	private boolean isPalying = false;

	int audio_length;
	int time_count = 0;
	private Handler mHanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Configs.DOWNLOAD_AUDIO_SUCCESS: // 音频下载成功
				String path = (String) msg.obj;
				audio_length = mRecord.getLength(path);
				mRecord.startPlaying(path);
				mHanlder.sendEmptyMessageDelayed(3, 1000);
				break;
			case Configs.DOWNLOAD_AUDIO_FAIL: // 音频下载失败
				mIvPlay.setImageResource(R.drawable.movie_play01);
				Toast.makeText(NewDetailActivity.this, "无内容",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				time_count++;
				if (time_count == audio_length || audio_length == 0) {
					isPalying = false;
					mIvPlay.setImageResource(R.drawable.movie_play01);
					time_count = 0;
				} else {
					mHanlder.sendEmptyMessageDelayed(3, 1000);
				}
				break;
			}
		}
	};

	/**
	 * 加载新鲜事图片
	 * 
	 * @param url
	 */
	private void handlerImg(String url) {
		if (!TextUtils.isEmpty(url) && !url.equalsIgnoreCase("null")) {
			url = Configs.IMAGE_URL_DOMAIN + url;
			mBitmap = ibc.getBitmap(url);
			if (mBitmap == null) {
				ImageLoader il = new ImageLoader(mImg, this,
						Configs.REFRESH_IMAGE);
				il.execute(url);
			} else {
				mImg.setImageBitmap(mBitmap);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.iv_play:
			if (isPalying) {
				isPalying = false;
				time_count = 0;
				audio_length = 0;
				mIvPlay.setImageResource(R.drawable.movie_play01);
				mRecord.stopPlaying();
				mHanlder.removeMessages(3);
			} else {
				isPalying = true;
				mIvPlay.setImageResource(R.drawable.movie_play01_tap);
				playAudio();
			}
			break;
		case R.id.iv_img: // 查看大图
			ImageView view = new ImageView(this);
			if (mNews != null) {
				mBitmap = ibc.getBitmap(Configs.IMAGE_URL_DOMAIN
						+ mNews.getIMAGE());
				if (null != mBitmap) {
					showImageInDialog(view,mBitmap);
				}
			}
			break;
		}
	}

	public void playAudio() {
		String path = Configs.VOICE_PATH
				+ AuxiliaryUtils.md5(IOUtil
						.getFilename(Configs.AUDIO_URL_DOMAIN
								+ mNews.getAUDIO()));
		File destFile = new File(path);
		if (!destFile.exists()) {
			AuxiliaryUtils.downLoadAudio(
					Configs.AUDIO_URL_DOMAIN + mNews.getAUDIO(), mHanlder);
		} else {
			audio_length = mRecord.getLength(path);
			mRecord.startPlaying(path);
			mHanlder.sendEmptyMessageDelayed(3, 1000);
		}
	}

	/**
	 * 查看大图
	 * 
	 * @param view
	 */
	private void showImageInDialog(View view,Bitmap mBitmap) {
		 ChatTextPopDialog.Builder chatbuilder=new ChatTextPopDialog.Builder(this);
         chatbuilder.setContentView(mBitmap);
         chatbuilder.create().show();
	}
}
