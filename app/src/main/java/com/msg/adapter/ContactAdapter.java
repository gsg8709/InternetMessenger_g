package com.msg.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msg.bean.Contacts;
import com.feihong.msg.sms.R;

public class ContactAdapter extends BaseAdapter {

	private ArrayList<Contacts> mRes = new ArrayList<Contacts>();
	private LayoutInflater mInflater;

	public ContactAdapter(Context context, ArrayList<Contacts> res) {
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
			convertView = mInflater.inflate(R.layout.expandable_child_item,
					null);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_child_content);
			holder.iv = (ImageView) convertView.findViewById(R.id.img2);
			holder.iv.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(mRes.get(position).getNAME());
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;
		ImageView iv;
	}
}
