package com.msg.ui;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.msg.common.Configs;
import com.msg.server.RetrievalSmsService;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.DialogUtil.DialogOnClickListener;
import com.msg.utils.UserManager;
import com.msg.utils.Utils;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;

/**
 * 设置
 * 
 * @author gongchao
 * 
 */
public class SettingActivity extends Activity implements OnClickListener,
		HttpHandlerListener {
	private ProgressDialog mDialog;
	private PopupWindow mPopupWindow;
	private LinearLayout mLayoutParent;
	private RelativeLayout mLayout_open_bus,layout_help;
	private CheckBox cb_screen, cb_ofhook;
	private String password = "";
	
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);

		mLayoutParent = (LinearLayout) findViewById(R.id.layout_parent);
		mLayout_open_bus = (RelativeLayout) this.findViewById(R.id.layout_open_bus);
		layout_help = (RelativeLayout) this.findViewById(R.id.layout_help);
		mLayout_open_bus.setOnClickListener(this);
		layout_help.setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title)).setText("设置");
		findViewById(R.id.btn_return).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_return).setOnClickListener(this);
		findViewById(R.id.btn_logout).setOnClickListener(this);
		findViewById(R.id.layout_password).setOnClickListener(this);
		findViewById(R.id.layout_user_info).setOnClickListener(this);
		findViewById(R.id.layout_update).setOnClickListener(this);
		findViewById(R.id.layout_screen_pop).setOnClickListener(this);
		findViewById(R.id.layout_ofhook_sm).setOnClickListener(this);

		cb_screen = (CheckBox) findViewById(R.id.cb_screen_pop);
		cb_ofhook = (CheckBox) findViewById(R.id.cb_ofhook_sms);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("保存中，请稍后");
		initPasswordDialog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_return:
			this.finish();
			break;
		case R.id.btn_logout:
			DialogUtil.dialogCustom(this, "确认退出?", null, "确定", "取消",
					new DialogOnClickListener() {

						@Override
						public void onDialogClick(DialogInterface dialog,
								int whichButton, int source) {
							if (whichButton == DialogInterface.BUTTON_POSITIVE) {
								logout();
							}
						}
					}, null).show();
			break;
		case R.id.layout_password:
			showPop();
			break;
		case R.id.layout_user_info:
			AuxiliaryUtils.toast(this, "layout_user_info");
			break;
		case R.id.layout_update:
			new UpdateVersion().execute(); 
			break;
		case R.id.layout_screen_pop:
			boolean screen = UserManager.getScreenPop(this);
			if (screen) {
				UserManager.saveScreenPop(this, false);
				cb_screen.setChecked(false);
			} else {
				UserManager.saveScreenPop(this, true);
				cb_screen.setChecked(true);
			}
			break;
		case R.id.layout_ofhook_sm:
			boolean ofhook = UserManager.getOfHookSms(this);
			if (ofhook) {
				UserManager.saveOfHookSms(this, false);
				cb_ofhook.setChecked(false);
			} else {
				UserManager.saveOfHookSms(this, true);
				cb_ofhook.setChecked(true);
			}
			break;
		case R.id.layout_open_bus://开通业务
//			DialogUtil.dialogMessage(this, "提示", "功能暂未开通", null, null, null, null);
			openBusiness();
			break;
		case R.id.layout_help:
			useHelp();//使用帮助
			break;
		default :
			break;
		}
		
	}

	private void logout() {
		if (!UserManager.getRemeberPwd(this)) {
			UserManager.cleanUserInfo(this);
		}
		UserManager.saveLogout(this, true);
		UserManager.saveScreenPop(this, false);
		UserManager.saveOfHookSms(this, false);
		stopService(new Intent(this, RetrievalSmsService.class));
		// new IMStorageDataBase(this).delTable();
		AuxiliaryUtils.closePage();
		this.finish();
	}

	private void initPasswordDialog() {
		LinearLayout layout_input = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_input_password_dialog, null);
		LinearLayout layout_button = (LinearLayout) layout_input
				.findViewById(R.id.layout_button);
		layout_button.setVisibility(View.VISIBLE);
		final EditText et_password = (EditText) layout_input
				.findViewById(R.id.edittext_group_name);
		final EditText et_password_retry = (EditText) layout_input
				.findViewById(R.id.edittext_group_name_retry);
		et_password.setHint("请输入新密码");
		et_password_retry.setHint("请重复输入密码");
		et_password.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		et_password_retry.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		final CheckBox cb_show = (CheckBox) layout_input
				.findViewById(R.id.cb_show_password);
		cb_show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cb_show.setChecked(cb_show.isChecked() ? true : false);
				if (cb_show.isChecked()) {
					et_password
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					et_password_retry
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					et_password.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					et_password_retry.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
				String password = et_password.getText().toString().trim();
				String password_retry = et_password_retry.getText().toString()
						.trim();
				et_password.setSelection(password.length());
				et_password_retry.setSelection(password_retry.length());
			}
		});
		mPopupWindow = new PopupWindow(layout_input, -1, -2);
		mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style2);
		mPopupWindow.setFocusable(true);
		layout_button.findViewById(R.id.btn_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String password = et_password.getText().toString().trim();
								
						String password_retry = et_password_retry.getText()
								.toString().trim();
						if (!TextUtils.isEmpty(password)
								&& !TextUtils.isEmpty(password_retry)) {
							if (password.equals(password_retry)) {
								handlerPassword(password);
								hidePop();
							} else {
								AuxiliaryUtils.toast(SettingActivity.this,
										"两次密码输入不一致");
								return;
							}
						} else {
							AuxiliaryUtils
									.toast(SettingActivity.this, "密码不能为空");
							return;
						}
					}
				});
		layout_button.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						hidePop();
					}
				});
	}

	private void showPop() {
		if (mPopupWindow.isShowing()) {
			hidePop();
		} else {
			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(200);
			mPopupWindow.showAtLocation(mLayoutParent, Gravity.TOP, 0, 0);
		}
	}

	private void hidePop() {
		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(150);
		mPopupWindow.dismiss();
	}

	private void handlerPassword(String password) {
		mDialog.show();
		String uid = UserManager.getUserinfo(this).getUID();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("UID", uid));
		params.add(new BasicNameValuePair("PWD", AuxiliaryUtils.md5(password)));
		NetHttpHandler handler = new NetHttpHandler(this);
		handler.setHttpHandlerListener(this);
		handler.execute(Configs.USER_UPDATE_ADDRESS, HttpRequestType.POST,
				params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			final byte[] data, final int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					String result = new String(data);
					if (result.contains("\"state\":true")) {
						AuxiliaryUtils.toast(SettingActivity.this, "密码修改成功");
						saveUserPassword(password);
					} else {
						AuxiliaryUtils.toast(SettingActivity.this, "密码修改失败");
					}
					dissmissDialog();
//					logout();
				}
			});
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(SettingActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(SettingActivity.this,
							R.string.msg_network_error);
				}
			});
			dissmissDialog();
			break;
		}
	}

	private void dissmissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
	
	/**
	 * 开通业务
	 */
	private void openBusiness() {
//		Intent intent = new Intent(this,OpenBuessinessActivity.class);
		Intent intent = new Intent(this,OpenBuessinessByWebActivity.class);
		this.startActivity(intent);
		
	}
	
	/**
	 * 使用帮助
	 */
	private void useHelp() {
		Intent intent = new Intent(this,HelpActivity.class);
		this.startActivity(intent);
	}
	
	private void saveUserPassword(String pw) {
		UserManager.getUserinfo(this).setPWD(pw);
	}
	
	class UpdateVersion extends AsyncTask<Void, Void, Void> {

		private String responseResult = "";
		private boolean resultCode = false;
		private String msg = "";
		
		@Override
		protected Void doInBackground(Void... arg0) {
			String baseURL = Configs.CHECKVERSION;
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			responseResult = Utils.getData(baseURL, requestParams);

			if (!TextUtils.isEmpty(responseResult)) {
				try {
					JSONObject jsonObject = new JSONObject(responseResult);
					resultCode = jsonObject.getBoolean("state");
					if(resultCode = true) {
						msg = jsonObject.getString("msg");
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					resultCode = false;
					e.printStackTrace();
				}
			} else {
				resultCode = false;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(!TextUtils.isEmpty(msg)) {
				if(!msg.equals(getVersion())) {
					doNewVersionUpdate();
				} else {
					AuxiliaryUtils.toast(SettingActivity.this,R.string.now_is_new_version);
					
				}
			} 			
		}
		
	}
	
	/**
	 * 
	 * @Title: doNewVersionUpdate
	 * @Description: 弹出更新版本对话框
	 * @param
	 * @return void
	 * @throws
	 */
	private void doNewVersionUpdate() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this); 
		builder.setMessage("发现最新版本，是否更新?");
		builder.setTitle("软件更新");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
	           public void onClick(DialogInterface dialog, int whichButton) {  
	        	   Uri uri = Uri.parse(Configs.DOWNLOAD_APK);
				   startActivity(new Intent(Intent.ACTION_VIEW, uri));
	           }  
	       });  
	       builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
	           public void onClick(DialogInterface dialog, int whichButton) {  
	           }  
	       });  
	       builder.create().show();  
		
	}
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return this.getString(R.string.can_not_find_version_name);
	    }
	}
}
