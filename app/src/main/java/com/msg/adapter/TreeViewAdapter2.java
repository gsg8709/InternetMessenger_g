package com.msg.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.common.Configs;

public class TreeViewAdapter2 extends BaseExpandableListAdapter {

	List<ContactGroup> mRes = new ArrayList<ContactGroup>();
	Context parentContext;
	int mPage;
	private HashMap statusHashMap = null; 

	public TreeViewAdapter2(Context view, ArrayList<ContactGroup> groups,
			int page,HashMap<String, Boolean> statusHashMap) {
		parentContext = view;
		this.mRes = groups;
		this.mPage = page;
		this.statusHashMap = statusHashMap;
	}

	public Contacts getChild(int groupPosition, int childPosition) {
		return mRes.get(groupPosition).getContacts().get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return mRes.get(groupPosition).getContacts().size();
	}

	/**
	 * 子列表item
	 * 
	 * @param context
	 * @return
	 */
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final ContactGroup group = mRes.get(groupPosition);
		Contacts c = group.getContacts().get(childPosition);
		RelativeLayout layout_child = (RelativeLayout) LayoutInflater.from(
				parentContext).inflate(R.layout.expandable_child_choose_item,
				null);
		((TextView) layout_child.findViewById(R.id.tv_child_content))
				.setText(group.getContacts().get(childPosition).getNAME());
		CheckBox cb_child = (CheckBox) layout_child
				.findViewById(R.id.cb_child_contacts);
		if (mPage == Configs.FROM_NOTE_PAGE) {
			cb_child.setVisibility(View.GONE);
		}
		Boolean nowStatus = (Boolean) statusHashMap.get(group.getContacts().get(childPosition).getNAME());//当前状态 
//		cb_child.setChecked(c.isCheck());
		
		
		 cb_child.setChecked(nowStatus); 
		
		
		return layout_child;
	}

	/**
	 * 组列表item
	 * 
	 * @param context
	 * @return
	 */
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final ContactGroup group = mRes.get(groupPosition);
		RelativeLayout group_view = (RelativeLayout) LayoutInflater.from(
				parentContext).inflate(R.layout.expandable_group_choose_item,
				null);
		TextView tv_content = (TextView) group_view
				.findViewById(R.id.tv_content);
		ImageView img2 = (ImageView) group_view.findViewById(R.id.img2);
		if (isExpanded) {
			img2.setImageResource(R.drawable.icon_open);
		} else {
			img2.setImageResource(R.drawable.icon_close);
		}
		tv_content.setText(group.getName());
		CheckBox cb_group = (CheckBox) group_view
				.findViewById(R.id.cb_group_contacts);
		if (mPage == Configs.FROM_NOTE_PAGE) {
			cb_group.setVisibility(View.GONE);
		}
		cb_group.setChecked(group.isCheck());
		cb_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (Contacts c : group.getContacts()) {
					boolean ischeck = c.isCheck();
					c.setCheck(ischeck ? false : true);
				}
				group.setCheck(group.isCheck() ? false : true);
				TreeViewAdapter2.this.notifyDataSetChanged();
			}
		});
		return group_view;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public ContactGroup getGroup(int groupPosition) {
		return mRes.get(groupPosition);
	}

	public int getGroupCount() {
		return mRes.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}
}
