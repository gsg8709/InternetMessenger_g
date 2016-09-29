package com.msg.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.msg.bean.SmsModeResult.SmsMode;
import com.msg.common.Configs;
import com.feihong.msg.sms.R;

public class SmsModeAdapter extends BaseAdapter {
	private ArrayList<SmsMode> mRes = new ArrayList<SmsMode>();
	private LayoutInflater mInflater;
	private int mType = -1;

	public SmsModeAdapter(Context context, ArrayList<SmsMode> res, int type) {
		mInflater = LayoutInflater.from(context);
		this.mRes = res;
		this.mType = type;
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
			convertView = mInflater.inflate(R.layout.sms_mode_list_item, null);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			if (mType == Configs.SEND_FOR_CHOOSE_SMS_MODE) {
				holder.cb_sms = (CheckBox) convertView
						.findViewById(R.id.cb_sms);
				holder.cb_sms.setVisibility(View.VISIBLE);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SmsMode note = mRes.get(position);
		holder.tv_content.setText(note.getMSG());
		return convertView;
	}

	class ViewHolder {
		TextView tv_content;
		CheckBox cb_sms;
	}
}
