package com.msg.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.WeakHashMap;

import android.graphics.Bitmap;

public class ImageBitmapCache {
	static private ImageBitmapCache cache;

	private WeakHashMap<String, MySoftRef> bitmapRefs;
	private ReferenceQueue<Bitmap> q;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class MySoftRef extends SoftReference<Bitmap> {
		private String _key = "";

		public MySoftRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}

	private ImageBitmapCache() {
		bitmapRefs = new WeakHashMap<String, MySoftRef>();
		q = new ReferenceQueue<Bitmap>();
	}

	public static ImageBitmapCache getInstance() {
		if (cache == null) {
			cache = new ImageBitmapCache();
		}
		return cache;
	}

	public void addCacheBitmap(Bitmap bmp, String key) {
		cleanCache();
		MySoftRef ref = new MySoftRef(bmp, q, key);
		bitmapRefs.put(key, ref);
	}

	public Bitmap getBitmap(String filename) {
		Bitmap bmp = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(filename)) {
			MySoftRef ref = (MySoftRef) bitmapRefs.get(filename);
			if (ref != null) {
				bmp = (Bitmap) ref.get();
			} else {
				return null;
			}
		}
		return bmp;
	}

	public void remove(String url) {
		bitmapRefs.remove(url);
	}

	private void cleanCache() {
		if (bitmapRefs.size() > 30) {
			int i = 0;
			for (WeakHashMap.Entry<String, MySoftRef> e : bitmapRefs.entrySet()) {
				++i;
				if (i > 30) {
					bitmapRefs.put(e.getKey(), null);
					bitmapRefs.remove(e.getKey());
				}
			}
		}

		MySoftRef ref = null;
		while ((ref = (MySoftRef) q.poll()) != null) {
			bitmapRefs.put(ref._key, null);
			bitmapRefs.remove(ref._key);
		}
	}

	/**
	 * 清除Cache内的全部内容
	 */
	public void clearCache() {
		cleanCache();
		if (bitmapRefs != null && bitmapRefs.size() != 0) {
			bitmapRefs.clear();
		}
		System.gc();
		System.runFinalization();
	}
}
