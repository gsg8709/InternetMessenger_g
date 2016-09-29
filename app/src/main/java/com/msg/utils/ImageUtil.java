package com.msg.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.msg.common.Configs;

public final class ImageUtil {

	/**
	 * 放大缩放图片
	 * 
	 * @param bitmap
	 *            要处理的图片
	 * @param height
	 *            最后的高度
	 * @param width
	 *            最后的宽度
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, Float height, Float width) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleW = width / (float) w;
		float scaleH = height / (float) h;
		matrix.postScale(scaleW, scaleH);
		Bitmap newBitmap = Bitmap
				.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newBitmap;
	}

	public static Bitmap reDrawBitMap(Bitmap bitmap) {
		DisplayMetrics dm = new DisplayMetrics();
		int rHeight = Configs.screenHeight;
		int rWidth = Configs.screenWidth;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		float zoomScale;
		if (rWidth / rHeight > width / height) {// 以高为准
			zoomScale = ((float) rHeight) / height;
		} else { // if(rWidth/rHeight<width/height)//以宽为准
			zoomScale = ((float) rWidth) / width;
		}
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(zoomScale, zoomScale);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				Configs.screenWidth, Configs.screenHeight, matrix, true);
		return resizedBitmap;

	}

	/**
	 * 圆角图片
	 * 
	 * @param bitmap
	 *            要处理的图片
	 * @param corner
	 *            角度
	 * @return
	 */
	public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float corner) {
		final int height = bitmap.getHeight();
		final int width = bitmap.getWidth();
		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0Xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, corner, corner, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap getHandlerDrawable(Bitmap bitmap, int dp) {
		return getRoundCornerBitmap(
				getShadeDrawable(zoomBitmap(bitmap, dip2px(dp) * 1.0f,
						dip2px(dp) * 1.0f)), 10.0f);
	}

	public static Bitmap getDefaultHandlerDrawable(Bitmap bitmap) {
		return getRoundCornerBitmap(getShadeDrawable(bitmap), 10.0f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		return (int) (dpValue * Configs.scale + 0.5f * (dpValue >= 0 ? 1 : -1));
	}

	public static Bitmap getShadeDrawable(Bitmap bitmap) {
		int PicWidth, PicHegiht;
		PicWidth = bitmap.getWidth();
		PicHegiht = bitmap.getHeight();

		Rect rect = new Rect(0, 0, PicWidth, PicHegiht);
		Bitmap innerBm = Bitmap.createBitmap(rect.width(), rect.height(),
				Bitmap.Config.ARGB_8888);
		Canvas innerCanvas = new Canvas(innerBm);
		Paint shadowPaint = new Paint();
		shadowPaint.setColor(Color.TRANSPARENT);
		shadowPaint.setAntiAlias(true);
		shadowPaint.setShadowLayer(5, 5, 5, 0xFF555555);
		innerCanvas.drawRect(rect, shadowPaint);
		innerCanvas.drawBitmap(bitmap, 0, 0, null);
		return innerBm;
	}

	/* 上传文件至Server的方法 */
	public static void uploadFile(Handler handler, String url, File file,
			String uid, int mime) {
		String httpUrl = url + "?uid=" + uid;
		HttpPost request = new HttpPost(httpUrl);
		HttpClient httpClient = new DefaultHttpClient();
		FileEntity entity = new FileEntity(file, "Application/oct-stream");
		HttpResponse response;
		try {
			request.setEntity(entity);
			entity.setContentEncoding("Application/oct-stream");
			response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (mime == 0) {
					handler.obtainMessage(BG_UPLOAD_SUCCESS,
							EntityUtils.toString(response.getEntity()))
							.sendToTarget();
				} else {
					handler.obtainMessage(HEAD_UPLOAD_SUCCESS,
							EntityUtils.toString(response.getEntity()))
							.sendToTarget();
				}
			}
		} catch (Exception e) {
			if (mime == 0) {
				handler.sendEmptyMessage(BG_UPLOAD_FAIL);
			} else {
				handler.sendEmptyMessage(HEAD_UPLOAD_FAIL);
			}
		}
	}

	public static boolean saveBitmap2file(Bitmap bmp, String path) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 80;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bmp.compress(format, quality, stream);
	}

	/**
	 * 图片合成
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createBitmap(Bitmap src, Bitmap watermark) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);
		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newb;
	}

	public static final int BG_UPLOAD_SUCCESS = 100;
	public static final int BG_UPLOAD_FAIL = 101;

	public static final int HEAD_UPLOAD_SUCCESS = 102;
	public static final int HEAD_UPLOAD_FAIL = 103;
}
