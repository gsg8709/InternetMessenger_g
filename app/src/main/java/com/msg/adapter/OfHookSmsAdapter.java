package com.msg.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msg.bean.SmsRuleResult.SmsRule;
import com.feihong.msg.sms.R;

public class OfHookSmsAdapter extends BaseAdapter {
	private ArrayList<SmsRule> mRes = new ArrayList<SmsRule>();
	private LayoutInflater mInflater;

	public OfHookSmsAdapter(Context context, ArrayList<SmsRule> res) {
		mInflater = LayoutInflater.from(context);
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
					.inflate(R.layout.ofhook_sms_list_item, null);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.tv_start = (TextView) convertView
					.findViewById(R.id.tv_start);
			holder.tv_end = (TextView) convertView.findViewById(R.id.tv_end);
			holder.iv_choose = (ImageView) convertView
					.findViewById(R.id.iv_choose);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SmsRule note = mRes.get(position);
		holder.tv_content.setText(note.getMSG());
		holder.tv_start.setText("开始：" + note.getSTARTTIME());
		holder.tv_end.setText("结束：" + note.getENDTIME());
		if (note.getSELECTED() == 1) {
			holder.iv_choose.setVisibility(View.VISIBLE);
		} else {
			holder.iv_choose.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_content;
		TextView tv_start;
		TextView tv_end;
		ImageView iv_choose;
	}
}
