package com.msg.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.msg.bean.Contacts;
import com.feihong.msg.sms.R;

public class ContactChooseAdapter extends BaseAdapter {

	private ArrayList<Contacts> mRes = new ArrayList<Contacts>();
	private LayoutInflater mInflater;

	public ContactChooseAdapter(Context context, ArrayList<Contacts> res) {
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
			convertView = mInflater.inflate(
					R.layout.expandable_child_choose_item, null);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_child_content);
			holder.cb_child = (CheckBox) convertView
					.findViewById(R.id.cb_child_contacts);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Contacts c = mRes.get(position);
		holder.cb_child.setChecked(c.isCheck());
		holder.tv_name.setText(mRes.get(position).getNAME());
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;
		CheckBox cb_child;
	}
}
