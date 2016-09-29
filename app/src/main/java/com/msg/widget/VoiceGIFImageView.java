package com.msg.widget;

import java.io.InputStream;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.feihong.msg.sms.R;

public class VoiceGIFImageView extends ImageView {

private Movie mMovie;
	
	private long mMovieStart;

	public VoiceGIFImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//从描述文件中读出gif的值，创建出Movie实例
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GIFView);
		int rescurceId = typedArray.getResourceId(R.styleable.GIFView_gif_test, 0);
		if(rescurceId >0) {
			InputStream is = context.getResources().openRawResource(rescurceId);
			mMovie = Movie.decodeStream(is);
		}
		typedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		long now = SystemClock.uptimeMillis();
		if(mMovieStart == 0) {
			mMovieStart = now;
		}
		if(mMovie != null) {
			int dur = mMovie.duration();// 动画时长
			if(dur == 0) {
				dur = 1000;
			}
			int relTime = (int) ((now - mMovieStart)% dur);//算出需要显示第几帧
			mMovie.setTime(relTime);//设置要显示的帧
			mMovie.draw(canvas, 0, 0);
			invalidate();
		}
	}
}
