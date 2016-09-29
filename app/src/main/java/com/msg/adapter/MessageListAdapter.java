package com.msg.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msg.bean.Contacts;
import com.msg.bean.ShowMsg;
import com.msg.common.Configs;
import com.feihong.msg.sms.R;
import com.msg.utils.TimeRender;

/**
 * 消息列表适配器
 * 
 * @author gongchao
 * 
 */
public class MessageListAdapter extends BaseAdapter {
	private ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>> maps;
	private LayoutInflater mInflater;

	public MessageListAdapter(Context context,
			ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>> maps) {
		this.maps = maps;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return maps.size();
	}

	@Override
	public Object getItem(int position) {
		return maps.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.msglist_rowchild, null);
			holder.name = (TextView) convertView.findViewById(R.id.childto);
			holder.icon = (ImageView) convertView.findViewById(R.id.groupIcon);
			holder.content = (TextView) convertView
					.findViewById(R.id.child_msg);
			holder.num = (TextView) convertView.findViewById(R.id.numIcon);
			holder.time = (TextView) convertView.findViewById(R.id.child_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HashMap<Contacts, ArrayList<ShowMsg>> item = (HashMap<Contacts, ArrayList<ShowMsg>>) getItem(position);
		if (item.size() > 0) {
			Contacts user = (Contacts) item.keySet().toArray()[0];
			ArrayList<ShowMsg> list = item.get(user);
			if (list.size() > 0) {
				ShowMsg msg = list.get(0);
				String nickname = user.getNAME();
				String username = user.getTEL();
				String message = msg.getMsg();
				String time = null;
				if(msg.getCreationData() != null) {
					 time = TimeRender.formatDate(Long.parseLong(msg
							.getCreationData()));
				}
				
				if (!TextUtils.isEmpty(nickname)
						&& !nickname.equalsIgnoreCase("null")) {
					holder.name.setText(nickname);
				} else {
					holder.name.setText(username);
				}
				holder.content.setText(message);
				if(!TextUtils.isEmpty(time)) {
					holder.time.setText(time);
				}
				int count = 0;
				for (ShowMsg showMsg : list) {
					int opened = showMsg.getMsgOpened();
					if (opened == Configs.MSG_UNREADED) {
						count++;
					}
				}
				if (count > 0) {
					holder.num.setVisibility(View.VISIBLE);
					String num = String.valueOf(count);
					if (count >= 999) {
						num = "999+";
					} else if (num.length() == 1) {
						num = " " + num + " ";
					}
					holder.num.setText(num);
				} else {
					holder.num.setVisibility(View.GONE);
				}
				if (user.getSEX() == 0) { //女生
					holder.icon.setImageResource(R.drawable.head_default_female);
				} 
				if(user.getSEX() == 1) {//男 生
					holder.icon.setImageResource(R.drawable.head_default_male);
				}
			}
		}
		return convertView;
	}

	class ViewHolder {
		TextView name;
		ImageView icon;
		TextView content;
		TextView time;
		TextView num;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
