package com.msg.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.msg.bean.Contacts;
import com.msg.bean.NoteResult.Note;
import com.msg.server.IMStorageDataBase;
import com.feihong.msg.sms.R;
import com.msg.utils.TimeRender;

public class NoteAdapter extends BaseAdapter {
	private ArrayList<Note> mRes = new ArrayList<Note>();
	private LayoutInflater mInflater;
	private IMStorageDataBase mDb;

	public NoteAdapter(Context context, ArrayList<Note> res) {
		mInflater = LayoutInflater.from(context);
		this.mRes = res;
		mDb = new IMStorageDataBase(context);
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
			convertView = mInflater.inflate(R.layout.note_list_item, null);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.tv_contact = (TextView) convertView
					.findViewById(R.id.tv_contact);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Note note = mRes.get(position);
		Contacts c = mDb.getFriendInfo(note.getCID());
		holder.tv_contact.setVisibility(View.VISIBLE);
		holder.tv_content.setText(note.getWORD());
		if (c != null) {
			holder.tv_contact.setText(c.getNAME());
		}
		holder.tv_time.setText(TimeRender.getDate(note.getEDITTIME()));
		return convertView;
	}

	class ViewHolder {
		TextView tv_content;
		TextView tv_contact;
		TextView tv_time;
	}
}
