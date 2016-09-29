package com.msg.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.msg.bean.ImConstant;
import com.msg.dao.OnPictureIntentResultListener;

/**
 * 照片工具类
 * @author gongchao
 *
 */
public class PhotoUtils extends Activity{

	public final static int TAKE_PHOTO_WITH_DATA = 2023;// 从相册读取图片
	public final static int CAMERA_WITH_DATA = 2024; // 拍照
	private static final int PHOTO_RESOULT = 2025;// 剪裁后的图片
	private OnPictureIntentResultListener cameraListener;
	private OnPictureIntentResultListener picListener;
	private boolean isCamera = false;
	/**
	 * 裁剪图片
	 * 
	 * @param context
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	public static void cropImageUri(Activity context, Uri uri, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * 选择相册图片
	 * 
	 * @param context
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	public static void chooseImage(Activity context, Uri uri, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", false);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * 裁剪图片
	 * 
	 * @param context
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	public static void cropImageUri(Activity context, Uri uri, int outputX,
			int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", outputX);
		intent.putExtra("aspectY", outputY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		context.startActivityForResult(intent, requestCode);
	}
	
	
	/**
	 * 剪裁图片
	 */
	public static void startPhotoZoom(Activity context,Uri uri,int requestCode) {
	      Intent intent = new Intent("com.android.camera.action.CROP");
	      intent.setDataAndType(uri, "image/*");
	      // crop为true是设置在开启的intent中设置显示的view可以剪裁
          intent.putExtra("crop", "true");

          // aspectX aspectY 是宽高的比例
	      intent.putExtra("aspectX", 1);
	      intent.putExtra("aspectY", 1);

	      // outputX,outputY 是剪裁图片的宽高
	      intent.putExtra("outputX", 300);
	      intent.putExtra("outputY", 300);
	      intent.putExtra("return-data", true);
	      intent.putExtra("noFaceDetection", true);
	      context.startActivityForResult(intent, requestCode);
	 }


	/**
	 * 选择相册图片
	 * 
	 * @param context
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	public static void chooseImage(Activity context, Uri uri, int outputX,
			int outputY, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", outputX);
		intent.putExtra("aspectY", outputY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * 拍照
	 * 
	 * @param context
	 * @param uri
	 * @param requestCode
	 */
	public static void takeImage(Activity context, Uri uri, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		context.startActivityForResult(intent, requestCode);
		
	}
	
	/**
	 * 启动相机
	 */
	public boolean startCamera(OnPictureIntentResultListener listener) {
		this.cameraListener = listener;
		isCamera = true;
		File cameraImgDir = new File(ImConstant.EHUI_DIR);
		if (!cameraImgDir.exists()) {
			cameraImgDir.mkdirs();
		}
		File cameraImg = new File(ImConstant.EHUI_CAMERA_PIC);
		if (cameraImg.exists()) {
			cameraImg.delete();
		}
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraImg));
			this.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			// TODO: handle exception
			System.gc();
			this.picListener = null;

			return false;
		}
		return true;
	}
	

	/**
	 * 剪裁结果
	 * */
	private void cropResult(Intent mIntent) {
		Bundle bundle = mIntent.getExtras();
		LogUtil.d("结果返回成功");
		if (null != bundle) {
			LogUtil.d("结果返回not null");
			Bitmap photo = bundle.getParcelable("data");
			if (null != photo) {
				LogUtil.d("结果返回 photo not null");
				if (null != this.cameraListener && isCamera) {
					this.cameraListener.onPictureIntentResult(photo);
				} else if (null != picListener) {
					this.picListener.onPictureIntentResult(photo);
				}
			}

		}
	}

	/**
	 * 读取图片
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static Bitmap decodeUriAsBitmap(Activity context, Uri uri) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Config.ALPHA_8;
		BitmapFactory.decodeFile(String.valueOf(uri), options);
		options.inSampleSize = computeInitialSampleSize(options, -1, 128*128);
		options.inJustDecodeBounds = false;
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
            fs = new FileInputStream(String.valueOf(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		bs = new BufferedInputStream(fs);
		try {
            bitmap =  BitmapFactory.decodeStream(bs, null, options);
        } catch (Exception e) {

        }
		return getRoundedCornerBitmap(bitmap);
	}


	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}


	/* 旋转图片 
     * @param angle 
     * @param bitmap 
     * @return Bitmap 
     */  
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
        //旋转图片 动作  
        Matrix matrix = new Matrix();;  
        matrix.postRotate(angle);  
        System.out.println("angle2=" + angle);  
        // 创建新的图片  
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        return resizedBitmap;  
    }
    
	public static Bitmap getRoundedCornerBitmap(Bitmap sourceBitmap) {
		try {
			Bitmap targetBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
					sourceBitmap.getHeight(), Config.ARGB_8888);
			// 得到画布
			Canvas canvas = new Canvas(targetBitmap);
			// 创建画笔
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			// 值越大角度越明显
			float roundPx = 20;
			float roundPy = 20;
			Rect rect = new Rect(0, 0, sourceBitmap.getWidth(),
					sourceBitmap.getHeight());
			RectF rectF = new RectF(rect);
			// 绘制
			canvas.drawARGB(0, 0, 0, 0);
			canvas.drawRoundRect(rectF, roundPx, roundPy, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(sourceBitmap, rect, rect, paint);
			return targetBitmap;

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static String getPicPathFromUri(Uri uri, Activity activity) {
        String value = uri.getPath();

        if (value.startsWith("/external")) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return value;
        }
    }
	
	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
        	   ExifInterface exifInterface = new ExifInterface(path);
               int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
               switch (orientation) {
               case ExifInterface.ORIENTATION_ROTATE_90:
                       degree = 90;
                       break;
               case ExifInterface.ORIENTATION_ROTATE_180:
                       degree = 180;
                       break;
               case ExifInterface.ORIENTATION_ROTATE_270:
                       degree = 270;
                       break;
               }     
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }

}
