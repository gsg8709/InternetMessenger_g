package com.msg.utils;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
	private String param;
	private ImageBitmapCache ibc;
	private Context context;
	private final WeakReference<View> imageViewReference; // 防止内存溢出
	private int mime;

	public ImageLoader(View view, Context context, int mime) {
		imageViewReference = new WeakReference<View>(view);
		init(context);
		this.mime = mime;
	}

	private void init(Context context) {
		ibc = ImageBitmapCache.getInstance();
		this.context = context;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		param = params[0];
		return loadImageFile(param);// 这里的Bitmap 会传到 onPostExecute
	}

	private Bitmap loadImageFile(String url) {
		try {
			Bitmap bmp = new FindImg().getBitMap(context, url, mime);
			if (bmp == null)
				bmp = new FindImg().getBitmapByUrl(context, url, mime);
			return bmp;
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null) {
			View view = imageViewReference.get();
			if (view != null) {
				if (bitmap != null) {
					ibc.addCacheBitmap(bitmap, param);
					if (view instanceof ImageView) {
						((ImageView) view).setImageBitmap(bitmap);
					}
				} else {
					System.out.println("loader---Null");
				}
			} else {
				ibc.addCacheBitmap(bitmap, param);
			}
		}
	}
}
