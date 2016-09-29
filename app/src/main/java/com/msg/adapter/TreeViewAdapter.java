package com.msg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msg.bean.Group;
import com.msg.bean.GroupParent;
import com.msg.server.IMStorageDataBase;
import com.msg.ui.ContactsActivity;
import com.msg.ui.GroupItemActivity;
import com.feihong.msg.sms.R;
import com.msg.utils.DialogUtil;
import com.msg.utils.DialogUtil.DialogOnClickListener;
import com.msg.utils.UserManager;

public class TreeViewAdapter extends BaseExpandableListAdapter {

	private String[] mResGroupLongClick = new String[] { "新建分组", "删除分组" };

	List<GroupParent> mRes = new ArrayList<GroupParent>();
	Activity parentContext;
	public static final int HANDLER_CALL = 0;
	public static final int HANDLER_SMS = 1;
	public static final int HANDLER_QUERY = 2;
	public static final int HANDLER_UPDATE = 3;
	public static final int HANDLER_DEL = 4;
	public static final int HANDLER_GROUP_UPDATE = 5;
	public static final int HANDLER_GROUP_DEL = 6;
	private ExpandableListView list;
	private IMStorageDataBase mDb;
	private LayoutInflater mInflater;

	private OnHandlerListener mListener;

	public interface OnHandlerListener {
		void onCreate();

		void onDelete();
	}

	public void setOnHandlerListener(OnHandlerListener lis) {
		this.mListener = lis;
	}

	public TreeViewAdapter(Activity view, ArrayList<GroupParent> gps,
			ExpandableListView list) {
		parentContext = view;
		this.mRes = gps;
		this.list = list;
		mDb = new IMStorageDataBase(view);
		mInflater = LayoutInflater.from(view);
	}

	public Group getChild(int groupPosition, int childPosition) {
		return mRes.get(groupPosition).getGroups().get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return mRes.get(groupPosition).getGroups().size();
	}

	/**
	 * 子列表item
	 * 
	 * @param context
	 * @return
	 */
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final GroupParent group = mRes.get(groupPosition);
		RelativeLayout layout_child = (RelativeLayout) LayoutInflater.from(
				parentContext).inflate(R.layout.expandable_child_item, null);
		TextView tv = ((TextView) layout_child
				.findViewById(R.id.tv_child_content));
		tv.setText(group.getGroups().get(childPosition).getName());
		tv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				LinearLayout mInputDialog = (LinearLayout) mInflater.inflate(
						R.layout.layout_input_dialog, null);
				final EditText et = (EditText) mInputDialog
						.findViewById(R.id.edittext_group_name);
				et.setHint("请输入群组名称");
				DialogUtil.dialogCustom(parentContext, "修改群组名称", mInputDialog,
						"确定", "", "取消", new DialogOnClickListener() {

							@Override
							public void onDialogClick(DialogInterface dialog,
									int whichButton, int source) {
								String name = et.getText().toString().trim();
								if (!TextUtils.isEmpty(name)) {
									mDb.updateGroups(name, group.getGroups()
											.get(childPosition).getId(), group
											.getGroups().get(childPosition)
											.getName());
									group.getGroups().get(childPosition)
											.setName(name);
									notifyDataSetChanged();
								}
								dialog.cancel();
							}
						}).show();
				return false;
			}
		});

		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(parentContext,
						GroupItemActivity.class);
				intent.putExtra("index", childPosition);
				parentContext.startActivityForResult(intent, 0);
			}
		});
		return layout_child;
	}

	/**
	 * 组列表item
	 * 
	 * @param context
	 * @return
	 */
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupParent group = mRes.get(groupPosition);
		RelativeLayout group_view = (RelativeLayout) LayoutInflater.from(
				parentContext).inflate(R.layout.expandable_group_item, null);
		TextView tv_content = (TextView) group_view
				.findViewById(R.id.tv_content);
		TextView tv_num = (TextView) group_view.findViewById(R.id.tv_num);
		ImageView img = (ImageView) group_view.findViewById(R.id.img1);
		ImageView img2 = (ImageView) group_view.findViewById(R.id.img2);
		if (isExpanded) {
			img2.setImageResource(R.drawable.icon_open);
		} else {
			img2.setImageResource(R.drawable.icon_close);
		}
		tv_num.setText("(" + group.getGroups().size() + ")");
		tv_num.setVisibility(View.VISIBLE);
		img.setVisibility(View.VISIBLE);
		tv_content.setText(group.getName());

		tv_content.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				DialogUtil.dialogList(parentContext, "群组", mResGroupLongClick,
						"", "", "", new DialogUtil.DialogOnClickListener() {

							@Override
							public void onDialogClick(DialogInterface dialog,
									int whichButton, int source) {
								switch (whichButton) {
								case 0:
									LinearLayout layout = (LinearLayout) mInflater
											.inflate(
													R.layout.layout_input_dialog,
													null);
									final EditText group_name = (EditText) layout
											.findViewById(R.id.edittext_group_name);
									group_name.setHint("请输入分组名称");
									DialogUtil.dialogCustom(parentContext,
											"新建群组", layout, "确定", "", "返回",
											new DialogOnClickListener() {

												@Override
												public void onDialogClick(
														DialogInterface dialog,
														int whichButton,
														int source) {
													if (source == DialogUtil.SOURCE_POSITIVE) {
														String name = group_name
																.getText()
																.toString()
																.trim();
														if (!TextUtils
																.isEmpty(name)) {
															mDb.saveGroups(
																	new Group(
																			0L,
																			group_name
																					.getText()
																					.toString()
																					.trim()),
																	UserManager
																			.getUserinfo(
																					parentContext)
																			.getUID());
															if (mListener != null) {
																mListener
																		.onCreate();
															}
														}
													}
												}
											}).show();
									break;
								case 1:
									if (mListener != null) {
										mListener.onDelete();
									}
									break;
								}
							}
						});
				return false;
			}
		});
		tv_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isExpanded) {
					list.collapseGroup(groupPosition);
				} else {
					list.expandGroup(groupPosition);
				}
			}
		});
		return group_view;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public GroupParent getGroup(int groupPosition) {
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
