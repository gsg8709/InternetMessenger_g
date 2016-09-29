package com.msg.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msg.bean.MiniCardListResult.MiniCard;
import com.msg.common.Configs;
import com.feihong.msg.sms.R;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.utils.TimeRender;

public class MiniCardAdapter extends BaseAdapter {
	private ArrayList<MiniCard> mRes = new ArrayList<MiniCard>();
	private LayoutInflater mInflater;
	private Bitmap mBitmap;
	private ImageBitmapCache ibc;
	private Context mContext;

	public MiniCardAdapter(Context context, ArrayList<MiniCard> res) {
		mInflater = LayoutInflater.from(context);
		ibc = ImageBitmapCache.getInstance();
		this.mContext = context;
		this.mRes = res;
	}

	@Override
	public int getCount() {
		return mRes.size();
	}

	@Override
	public Object getItem(int position) {
		return mRes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater
					.inflate(R.layout.mini_card_list_item2, null);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MiniCard note = mRes.get(position);
		holder.tv_content.setText(note.getTITLE());
		handlerImg(holder.iv_head, note.getIMAGE());
		holder.tv_time.setText(TimeRender.getDate(note.getCTIME()));
		return convertView;
	}

	/**
	 * 加载新鲜事图片
	 * 
	 * @param url
	 */
	private void handlerImg(ImageView icon, String url) {
		icon.setImageResource(R.drawable.icon_default);
		if (!TextUtils.isEmpty(url) && !url.equalsIgnoreCase("null")) {
			url = Configs.SIMAGE_URL_DOMAIN + url;
			mBitmap = ibc.getBitmap(url);
			if (mBitmap == null) {
				ImageLoader il = new ImageLoader(icon, mContext,
						Configs.REFRESH_SIMAGE);
				il.execute(url);
			} else {
				icon.setImageBitmap(mBitmap);
			}
		}
	}

	class ViewHolder {
		TextView tv_content;
		ImageView iv_head;
		TextView tv_time;
	}
}
