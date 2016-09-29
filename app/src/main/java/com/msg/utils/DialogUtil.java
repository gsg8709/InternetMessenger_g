package com.msg.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 此类主要集中创建diglog，避免到处都是dialog的代码
 * 
 * @author changxf
 * 
 */
public class DialogUtil {
	/**
	 * 正向button返回
	 */
	public static final int SOURCE_POSITIVE = -5;
	/**
	 * 中间button返回
	 */
	public static final int SOURCE_NEUTRAL = -6;
	/**
	 * 反向button返回
	 */
	public static final int SOURCE_NEGATIVE = -7;

	/**
	 * list view返回
	 */
	public static final int SOURCE_LIST_VIEW = -8;

	/**
	 * radioButton
	 */
	public static final int SOURCE_CHECK_BOX = -9;

	/**
	 * radioButton
	 */
	public static final int SOURCE_RADIO_BUTTON = -10;

	/**
	 * 简单消息对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param message
	 *            内容
	 * @param positiveButtonText
	 *            正向button
	 * @param neutralButtonText
	 *            中间button
	 * @param negativeButtonText
	 *            反向button
	 * @param onClickListener
	 *            点击事件监听
	 */
	public static void dialogMessage(Context context, String title,
			String message, String positiveButtonText,
			String neutralButtonText, String negativeButtonText,
			final DialogOnClickListener onClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		button(title, builder, positiveButtonText, neutralButtonText,
				negativeButtonText, onClickListener);
	}

	/**
	 * 等待的对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param msg
	 *            内容
	 * @param onClickListener
	 *            点击事件监听
	 */
	public static ProgressDialog dialogProgress(Context context, String title,
			String msg, final DialogOnClickListener onClickListener) {
		// 创建ProgressDialog对象
		ProgressDialog mProgressDialog = new ProgressDialog(context);
		// 设置进度条风格，风格为圆形，旋转的
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 标题
		mProgressDialog.setTitle(title);
		// 设置ProgressDialog提示信息
		mProgressDialog.setMessage(msg);
		// 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
		mProgressDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回键取消
		mProgressDialog.setCancelable(true);
		// 让ProgressDialog显示
		mProgressDialog.show();

		return mProgressDialog;
	}

	/**
	 * 等待的对话框
	 * 
	 * @param context
	 * @param msg
	 *            内容
	 */
	public static ProgressDialog dialogProgress(Context context, String msg) {

		return dialogProgress(context, null, msg, null);

	}

	/**
	 * 创建显示list对话
	 * 
	 * @param context
	 * @param title
	 * @param data
	 *            list数据
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	public static void dialogList(Context context, String title, String[] data,
			String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListener onClickListener) {
		if (null == data || data.length == 0) {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setItems(data, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if (null != onClickListener) {
					onClickListener.onDialogClick(dialog, whichButton,
							SOURCE_LIST_VIEW);
				}
			}
		});
		button(title, builder, positiveButtonText, neutralButtonText,
				negativeButtonText, onClickListener);
	}

	/**
	 * 单选列表对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param data
	 *            list数据
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	public static void dialogRadioButton(Context context, String title,
			String[] data, String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListener onClickListener) {
		dialogRadioButton(context, title, data, positiveButtonText,
				neutralButtonText, negativeButtonText, onClickListener, 0);
	}

	/**
	 * 单选列表对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param data
	 *            list数据
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 * @param checkedIndex
	 *            选中index
	 */
	public static void dialogRadioButton(Context context, String title,
			String[] data, String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListener onClickListener, int checkedIndex) {
		if (null == data || data.length == 0 || checkedIndex >= data.length) {
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setSingleChoiceItems(data, checkedIndex,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickListener.onDialogClick(dialog, which,
								SOURCE_RADIO_BUTTON);
					}
				});
		button(title, builder, positiveButtonText, neutralButtonText,
				negativeButtonText, onClickListener);
	}

	/**
	 * 一般复选列表对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param data
	 *            list数据
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 * @param choiceItems
	 *            存放的是itme的索引 ,就是把要设为true的item的索引放进去，进来时会初始化列表
	 */
	public static void dialogCheckBox(Context context, String title,
			String[] data, final ArrayList<Object> choiceItems,
			String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListenerWithResult onClickListener) {
		if (null == data || data.length == 0) {
			return;
		}
		final boolean[] checkedItems = new boolean[data.length]; // 自动记忆选择记录
		initCheckedItem(checkedItems, choiceItems);// 初始化checkedItems

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMultiChoiceItems(data, checkedItems,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						int index = checkList(choiceItems, which);
						/**
						 * 如果被点击的item在list中则将其从list中删除
						 */
						if (index >= 0) {
							choiceItems.remove(index);
						} else {
							/**
							 * 如果被点击的item不在list中则将其加入list
							 */
							choiceItems.add(which);
						}
						onClickListener.onDialogClick(dialog, which,
								SOURCE_CHECK_BOX, choiceItems);
					}
				});
		buttonWithResult(title, builder, positiveButtonText, neutralButtonText,
				negativeButtonText, choiceItems, onClickListener);
	}

	/**
	 * 带“全选”和“全不选”的多选对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param data
	 *            list数据
	 * @param choiceItems
	 *            历史记录
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	public static void dialogCheckBoxAllSelect(Context context, String title,
			final String[] data, final ArrayList<Object> choiceItems,
			String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListenerWithResult onClickListener) {
		if (null == data || data.length == 0) {
			return;
		}
		final boolean[] checkedItems = new boolean[data.length]; // 自动记忆选择记录
		initCheckedItem(checkedItems, choiceItems);// 初始化checkedItems
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMultiChoiceItems(data, checkedItems,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						int index = checkList(choiceItems, which);
						/**
						 * 如果被点击的item在list中则将其从list中删除
						 */
						if (index >= 0) {
							choiceItems.remove(index);
						} else {
							/**
							 * 如果被点击的item不在list中则将其加入list
							 */
							choiceItems.add(which);
						}

						ListView listView = ((AlertDialog) dialog)
								.getListView();
						if (listView == null) {
							System.out.println("listView is null");
							return;
						}
						if (which == 0) {
							if (isChecked) {
								// 如果“不限价格”被选中，则把其它的全部选中
								for (int i = 1; i < checkedItems.length; i++) {
									if (checkList(choiceItems, i) < 0) {
										choiceItems.add(i);
										listView.setItemChecked(i, true);
									}
								}
								initCheckedItem(checkedItems, choiceItems);// 初始化checkedItems
								// builder.setMultiChoiceItems(prices,
								// checkedItems,
								// this);
							} else {
								// 如果“不限价格”没有被选中，则把其它的全部清空
								choiceItems.clear();
								initCheckedItem(checkedItems, choiceItems);// 初始化checkedItems
								for (int i = 1; i < checkedItems.length; i++) {
									listView.setItemChecked(i, false);
								}
							}
						} else {
							if (checkedItems[0]) {
								int i = checkList(choiceItems, 0);
								if (i >= 0) {
									choiceItems.remove(i);
								}
								initCheckedItem(checkedItems, choiceItems);// 初始化checkedItems
								listView.setItemChecked(0, false);
							}
						}
						onClickListener.onDialogClick(dialog, which,
								SOURCE_CHECK_BOX, choiceItems);
					}
				});
		buttonWithResult(title, builder, positiveButtonText, neutralButtonText,
				negativeButtonText, choiceItems, onClickListener);
	}

	/**
	 * 判断element是否在list中
	 * 
	 * @param list
	 * @param element
	 * @return
	 */
	final static int checkList(List<Object> list, int element) {
		for (int i = 0; i < list.size(); i++) {
			if (element == (Integer) list.get(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 初始化checkedItems, choiceItems中存放的是itme的索引；checkedItems中存放的是第个索引的状态
	 * 如果checkedItems的索引在choiceItems中，则设为选中状态（true） 否则设为false
	 * 
	 * @param checkedItems
	 * @param choiceItems
	 */
	private static void initCheckedItem(boolean[] checkedItems,
			ArrayList<Object> choiceItems) {
		for (int i = 0; i < checkedItems.length; i++) {
			checkedItems[i] = false;
		}
		if (choiceItems != null) {
			for (int i = 0; i < choiceItems.size(); i++) {
				checkedItems[(Integer) choiceItems.get(i)] = true;
			}
		}
	}

	/**
	 * 自定义布局对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param view
	 *            布局对象
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	public static AlertDialog dialogCustom(Context context, String title,
			View view, String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListener onClickListener) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		// button(title, builder, positiveButtonText, neutralButtonText,
		// negativeButtonText, onClickListener);
		if (null != title && title.trim().length() > 0) {
			builder.setTitle(title);
		}
		if (null != positiveButtonText
				&& positiveButtonText.trim().length() > 0) {
			builder.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_POSITIVE);
							}
						}
					});
		}
		if (null != neutralButtonText && neutralButtonText.trim().length() > 0) {
			builder.setNeutralButton(neutralButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEUTRAL);
							}
						}
					});
		}
		if (null != negativeButtonText
				&& negativeButtonText.trim().length() > 0) {
			builder.setNegativeButton(negativeButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEGATIVE);
							}
						}
					});
		}
		dialog = builder.create();
		return dialog;
	}

	/**
	 * 自定义布局对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param view
	 *            布局对象
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	public static AlertDialog dialogCustom(Context context, String title,
			View view, String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListener onClickListener, boolean flag) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		builder.setCancelable(flag);
		// button(title, builder, positiveButtonText, neutralButtonText,
		// negativeButtonText, onClickListener);
		if (null != title && title.trim().length() > 0) {
			builder.setTitle(title);
		}
		if (null != positiveButtonText
				&& positiveButtonText.trim().length() > 0) {
			builder.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_POSITIVE);
							}
						}
					});
		}
		if (null != neutralButtonText && neutralButtonText.trim().length() > 0) {
			builder.setNeutralButton(neutralButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEUTRAL);
							}
						}
					});
		}
		if (null != negativeButtonText
				&& negativeButtonText.trim().length() > 0) {
			builder.setNegativeButton(negativeButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEGATIVE);
							}
						}
					});
		}
		dialog = builder.create();
		return dialog;
	}

	/**
	 * 自定义布局对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param view
	 *            布局对象
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	public static AlertDialog dialogCustom(Context context, String title,
			View view, String positiveButtonText, String negativeButtonText,
			final DialogOnClickListener onClickListener,
			final DialogOnKeyListener onKeyListener) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		// button(title, builder, positiveButtonText, neutralButtonText,
		// negativeButtonText, onClickListener);
		if (null != title && title.trim().length() > 0) {
			builder.setTitle(title);
		}
		if (null != positiveButtonText
				&& positiveButtonText.trim().length() > 0) {
			builder.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_POSITIVE);
							}
						}
					});
		}

		if (null != negativeButtonText
				&& negativeButtonText.trim().length() > 0) {
			builder.setNegativeButton(negativeButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEGATIVE);
							}
						}
					});
		}
		builder.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent keyEvent) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						|| keyCode == KeyEvent.KEYCODE_HOME) {
					onKeyListener.onKeyClick();
				} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}

		});
		dialog = builder.create();
		return dialog;
	}

	/**
	 * listview中每个item是自定义view布局的对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param adapter
	 *            adapter
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param listener
	 *            监听list列表项事件
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	public static void dialogCustomListView(Context context, String title,
			ListAdapter adapter, String positiveButtonText,
			String neutralButtonText, String negativeButtonText,
			DialogInterface.OnClickListener listener,
			final DialogOnClickListener onClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setAdapter(adapter, listener);
		button(title, builder, positiveButtonText, neutralButtonText,
				negativeButtonText, onClickListener);
	}

	/**
	 * 这三个按钮每个方法都要用到，所以提取出来
	 * 
	 * @param builder
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */
	private static void button(String title, AlertDialog.Builder builder,
			String positiveButtonText, String neutralButtonText,
			String negativeButtonText,
			final DialogOnClickListener onClickListener) {
		if (null != title && title.trim().length() > 0) {
			builder.setTitle(title);
		}
		if (null != positiveButtonText
				&& positiveButtonText.trim().length() > 0) {
			builder.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_POSITIVE);
							}
						}
					});
		}
		if (null != neutralButtonText && neutralButtonText.trim().length() > 0) {
			builder.setNeutralButton(neutralButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEUTRAL);
							}
						}
					});
		}
		if (null != negativeButtonText
				&& negativeButtonText.trim().length() > 0) {
			builder.setNegativeButton(negativeButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEGATIVE);
							}
						}
					});
		}
		builder.create().show();
	}

	/**
	 * 这三个按钮每个方法都要用到，所以提取出来
	 * 
	 * @param title
	 *            对话框的标题
	 * @param builder
	 * @param positiveButtonText
	 *            正向按钮的文字描，没有则不创建这个按钮
	 * @param neutralButtonText
	 *            中间按钮的文字描，没有则不创建这个按钮
	 * @param negativeButtonText
	 *            反向按钮的文字描 ，没有则不创建这个按钮
	 * @param choiceItems
	 *            要返回的数据
	 * @param onClickListener
	 *            监听onClick函数被触发的事件
	 */

	private static void buttonWithResult(String title,
			AlertDialog.Builder builder, String positiveButtonText,
			String neutralButtonText, String negativeButtonText,
			final ArrayList<Object> choiceItems,
			final DialogOnClickListenerWithResult onClickListener) {
		if (null != title && title.trim().length() > 0) {
			builder.setTitle(title);
		}
		if (null != positiveButtonText
				&& positiveButtonText.trim().length() > 0) {
			builder.setPositiveButton(positiveButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_POSITIVE,
										choiceItems);
							}
						}
					});
		}
		if (null != neutralButtonText && neutralButtonText.trim().length() > 0) {
			builder.setNeutralButton(neutralButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEUTRAL,
										choiceItems);
							}
						}
					});
		}
		if (null != negativeButtonText
				&& negativeButtonText.trim().length() > 0) {
			builder.setNegativeButton(negativeButtonText,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (null != onClickListener) {
								onClickListener.onDialogClick(dialog,
										whichButton, SOURCE_NEGATIVE,
										choiceItems);
							}
						}
					});
		}
		builder.create().show();
	}

	/**
	 * 通过此接口将dialog的onclick反转给了调用者
	 * 
	 * @author wanglun
	 * 
	 */
	public interface DialogOnClickListener {
		void onDialogClick(DialogInterface dialog, int whichButton, int source);
	}

	/**
	 * 通过此接口将dialog的onkey反转给了调用者
	 * 
	 * @author gongxq
	 * 
	 */

	public interface DialogOnKeyListener {
		void onKeyClick();
	}

	/**
	 * 通过此接口将dialog的onclick反转给了调用者
	 * 
	 * @author wanglun
	 * 
	 */
	public interface DialogOnClickListenerWithResult {
		void onDialogClick(DialogInterface dialog, int whichButton, int source,
				Object resultData);
	}

	/**
	 * <p>
	 * 在页面上现实Toast提示
	 * </p>
	 * 
	 * @author <a href="mailto:wangyf@mapbar.com">wangyf</a>
	 * @date 2011-5-25 下午03:13:12
	 */
	public static void showToast(Context con, int resId) {
		Toast.makeText(con, resId, Toast.LENGTH_LONG).show();
	}

	/**
	 * <p>
	 * 在页面上现实Toast提示
	 * </p>
	 * 
	 * @author <a href="mailto:wangyf@mapbar.com">wangyf</a>
	 * @date 2011-5-25 下午03:13:12
	 */
	public static void showToast(Context con, CharSequence text) {
		Toast.makeText(con, text, Toast.LENGTH_LONG).show();
	}
}
