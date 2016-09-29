package com.msg.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.bean.Contacts;
import com.msg.bean.ShowMsg;
import com.msg.common.Configs;
import com.msg.utils.ImageBitmapCache;
import com.msg.utils.ImageLoader;
import com.msg.utils.PhotoUtils;
import com.msg.utils.TimeRender;

import java.text.SimpleDateFormat;
import java.util.List;

@SuppressLint("CutPasteId")
public class ChatAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ShowMsg> list;
	private Context mContext;
	private ImageBitmapCache ibc;
	private Contacts mUser;
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy年MM月dd日 HH:mm:ss"); //日期格式

	public ChatAdapter(Context context, List<ShowMsg> listMsg, Contacts user,
			Handler handler) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = listMsg;
		this.mContext = context;
		this.mUser = user;
		ibc = ImageBitmapCache.getInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	/**
	 * bug：缩略图刷不出来
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final ShowMsg item = list.get(position);
		ImageView icon = null;
		if (item.getMsgOrient() == Configs.RECEIVE_MSG) {
			convertView = this.inflater.inflate(
					R.layout.chathistory_row_layout_receive, null);
			icon = (ImageView) convertView
					.findViewById(R.id.chathistory_row_thumbnail);
			int sex = mUser.getSEX();
			if (sex == 1) {
				icon.setImageResource(R.drawable.head_default_male);
			} else {
				icon.setImageResource(R.drawable.head_default_female);
			}
		} else {
			convertView = this.inflater.inflate(
					R.layout.chathistory_row_layout_send, null);
			icon = (ImageView) convertView
					.findViewById(R.id.chathistory_row_thumbnail);
		}

		LinearLayout layout_inter_sms = (LinearLayout) convertView
				.findViewById(R.id.layout_inter_sms);
		TextView tv_msg = (TextView) convertView
				.findViewById(R.id.chat_row_msg);
		TextView timeTv = (TextView) convertView
				.findViewById(R.id.chatting_time);
//		String createTime = item.getCreationData();
//		createTime = dateFormat.format(Long.parseLong(createTime));
//		createTime = TimeUtils.converTime(Long.parseLong(createTime));
//		timeTv.setText(TimeUtils.getTimeAgo2(createTime));
		if(!TextUtils.isEmpty(item.getCreationData())) {
			String time = TimeRender.formatDate(Long.parseLong(item.getCreationData()));
			timeTv.setText(time);
		}
		
		int msg_type = item.getMsgType();
		if (msg_type == Configs.MSG_TYPE_INTER_SMS
				&& (!TextUtils.isEmpty(item.getTitle())
						|| !TextUtils.isEmpty(item.getImagePath())
						|| !TextUtils.isEmpty(item.getImageUrl())
						|| !TextUtils.isEmpty(item.getVoicePath()) || !TextUtils
							.isEmpty(item.getVoiceUrl()))) {
			layout_inter_sms.setVisibility(View.VISIBLE);
			tv_msg.setVisibility(View.GONE);
			String url = item.getImageUrl();
			String path = item.getImagePath();
			ImageView iv = (ImageView) convertView.findViewById(R.id.iv_sms);
			if (TextUtils.isEmpty(url) && TextUtils.isEmpty(path)) {
				iv.setVisibility(View.GONE);
			} else {
				iv.setVisibility(View.VISIBLE);
				handlerImage(item, iv);
			}

			TextView tv_sms_title = (TextView) convertView
					.findViewById(R.id.tv_sms_title);
			tv_sms_title.setText(item.getTitle());

			TextView tv_sms_content = (TextView) convertView
					.findViewById(R.id.tv_sms_content);
			tv_sms_content.setText(item.getMsg());
		} else {
			layout_inter_sms.setVisibility(View.GONE);
			tv_msg.setVisibility(View.VISIBLE);
		}
		tv_msg.setLongClickable(false);
		tv_msg.setText(item.getMsg());
		
	
		return convertView;
	}

	private Bitmap mBitmap;

	private void handlerImage(ShowMsg item, ImageView icon) {
		String url = item.getImageUrl();
		String path = item.getImagePath();
		if (!TextUtils.isEmpty(path)) {
			Uri uri = Uri.parse((String) path);
			Bitmap bitmap = PhotoUtils.decodeUriAsBitmap((Activity) mContext,uri);
			icon.setImageBitmap(bitmap);
//			Picasso.with(mContext).load(new File(path)).into(icon);
		} else if (!TextUtils.isEmpty(url)) {
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
}
