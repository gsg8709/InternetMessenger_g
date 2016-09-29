package com.msg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.msg.common.Configs;

public class FindImg {

	public Bitmap getBitMap(Context context, String url, int mime) {
		String path;
		if (mime == Configs.REFRESH_HEAD) {
			path = Configs.HEAD_PATH
					+ AuxiliaryUtils.md5(IOUtil.getFilename(url));
		} else if (mime == Configs.REFRESH_SIMAGE) {
			path = Configs.SIMAGE_PATH
					+ AuxiliaryUtils.md5(IOUtil.getFilename(url));
		} else if (mime == Configs.REFRESH_IMAGE) {
			path = Configs.IMAGE_PATH
					+ AuxiliaryUtils.md5(IOUtil.getFilename(url));
		} else if (mime == Configs.REFRESH_MIME_HEAD) {
			path = Configs.MIME_HEAD_PATH + AuxiliaryUtils.md5(url);
		} else {
			path = Configs.IMAGE_PATH + AuxiliaryUtils.md5(url);
		}

		File file = new File(path);
		if (file.exists()) {
			return readBitMap(context, path, mime);
		}
		return null;
	}

	public Bitmap getBitmapByUrl(Context context, String url, int mime) {
		InputStream is = null;
		boolean hasCard = AuxiliaryUtils.hasSDCard();
		try {
			is = getImageByUrl(url);
			if (mime == Configs.REFRESH_HEAD) {
				if (hasCard) {
					boolean store = AuxiliaryUtils.storeHead(is, url);
					if (store) {
						return getBitMap(context, url, mime);
					} else {
						return null;
					}
				} else {
					try {
						return readBitMap(context, is, mime);
					} catch (Exception e) {
						return null;
					} finally {
					}
				}
			} else if (mime == Configs.REFRESH_IMAGE) {
				if (hasCard) {
					boolean store = AuxiliaryUtils.storeImage(is, url);
					if (store) {
						return getBitMap(context, url, mime);
					} else {
						return null;
					}
				} else {
					try {
						return readBitMap(context, is, mime);
					} catch (Exception e) {
						return null;
					} finally {
					}
				}
			} else if (mime == Configs.REFRESH_SIMAGE) {
				if (hasCard) {
					boolean store = AuxiliaryUtils.storeSImage(is, url);
					if (store) {
						return getBitMap(context, url, mime);
					} else {
						return null;
					}
				} else {
					try {
						return readBitMap(context, is, mime);
					} catch (Exception e) {
						return null;
					} finally {
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private InputStream getImageByUrl(String uri) throws MalformedURLException {
		URL url = new URL(uri);
		URLConnection conn;
		InputStream is = null;
		try {
			conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap readBitMap(Context context, String path, int mime) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = computeSampleSize(opt, -1, 128 * 128);
		InputStream is;
		try {
			is = new FileInputStream(new File(path));
			return BitmapFactory.decodeStream(is, null, opt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap readBitMap(Context context, InputStream is, int mime) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = computeSampleSize(opt, -1, 128 * 128);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) &&

		(minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 最省内存读取resId图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
}
