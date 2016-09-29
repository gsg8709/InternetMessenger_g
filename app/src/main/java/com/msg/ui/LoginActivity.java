package com.msg.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.feihong.msg.sms.R;
import com.google.gson.Gson;
import com.msg.bean.CurrentUserBean;
import com.msg.bean.LoginResult;
import com.msg.bean.User;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.DialogUtil;
import com.msg.utils.Md5Utils;
import com.msg.utils.UserManager;
import com.sharesns.net.HttpHandler.HttpHandlerListener;
import com.sharesns.net.HttpHandler.HttpRequestType;
import com.sharesns.net.NetHttpHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements OnClickListener,
		HttpHandlerListener {
	private EditText mEtIdentifier, mEtPassword,verif_et;
	private Button mVerifCodeBtn;
	private ImageView verif_imageview;
	private ProgressDialog mDialog;
	private CheckBox mCheckBox;
	private String pw = "";
	private String id = "";
	private User user = null;
	public String Imei;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);

		((TextView) findViewById(R.id.tv_title)).setText("互联信使");
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_register).setOnClickListener(this);
		mEtIdentifier = (EditText) findViewById(R.id.edittext_identifier);
		mEtPassword = (EditText) findViewById(R.id.edittext_password);
		mVerifCodeBtn = (Button) findViewById(R.id.send_verification_code);
		verif_imageview = (ImageView) findViewById(R.id.verif_imageview);
		verif_et = (EditText) findViewById(R.id.verif_et);
		mVerifCodeBtn.setOnClickListener(this);
	    Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
				.getDeviceId();
		mCheckBox = (CheckBox) findViewById(R.id.cb_rem_password);
		boolean islogout = UserManager.getLogout(this);
		User user = UserManager.getUserinfo(this);
		if(!islogout) {
			if (user != null) {
				String name = Md5Utils.convertMD5(Md5Utils.convertMD5(user.getUID()));
				String pwd =  Md5Utils.convertMD5(Md5Utils.convertMD5(user.getPWD()));
				if (!TextUtils.isEmpty(name)) {
					mEtIdentifier.setText(name);
				}
				if (!TextUtils.isEmpty(pwd)) {
					mEtPassword.setText(pwd);
				}
			}
		}

		mDialog = new ProgressDialog(this);
		mDialog.setMessage("登陆中，请稍后");

		Bundle data = getIntent().getExtras();
		if (null != data) {
			boolean offline = data.getBoolean("offline", false);
			if (offline) {
				DialogUtil.dialogMessage(this, "下线通知", "您的账号已在另一台设备上登录，请重新登录",
						"确定", "", "", null);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			handlerLogin();
			break;
		case R.id.btn_register:
//			 startActivity(new Intent(this, RegisterActivity.class));
			mEtIdentifier.setText("13601156965");
			mEtPassword.setText("13601156965");
			break;
		case R.id.send_verification_code:
			verif_imageview.setVisibility(View.VISIBLE);
			mVerifCodeBtn.setText("换一张");
			Picasso.with(this).invalidate(Configs.CONTACT_GET_VERYCODE);
			Picasso.with(this).load(Configs.CONTACT_GET_VERYCODE).into(verif_imageview);
			Picasso.with(this).invalidate(Configs.CONTACT_GET_VERYCODE);
			break;
		}

	}

	private void handlerLogin() {
		id = mEtIdentifier.getText().toString().trim();
		pw = mEtPassword.getText().toString().trim();
		String verifyCode = verif_et.getText().toString().trim();
		if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(pw) && !TextUtils.isEmpty(verifyCode)) {
			mDialog.show();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UID", id));
			params.add(new BasicNameValuePair("PWD", AuxiliaryUtils.md5(pw)));
			params.add(new BasicNameValuePair("IMEI", Configs.imei));
			params.add(new BasicNameValuePair("AUTH", verifyCode));
			NetHttpHandler handler = new NetHttpHandler(this);
			handler.setHttpHandlerListener(this);
			handler.execute(Configs.USER_LOGIN_ADDRESS, HttpRequestType.POST,
					params, true, NetHttpHandler.RECEIVE_DATA_MIME_STRING);
			Log.e("Goo", Configs.USER_LOGIN_ADDRESS + params.toString());
		} else {
			if (TextUtils.isEmpty(id)) {
				AuxiliaryUtils.toast(this, "用户名不能为空");
				return;
			}
			if (TextUtils.isEmpty(pw)) {
				AuxiliaryUtils.toast(this, "密码不能为空");
			}
			if (TextUtils.isEmpty(verifyCode)) {
				AuxiliaryUtils.toast(this, "验证码不能为空");
			}
		}
	}

	@Override
	public void onResponse(int responseCode, String responseStatusLine,
			byte[] data, int mime) {
		switch (responseCode) {
		case HttpURLConnection.HTTP_OK:
			if (null == data || data.length == 0) {
				return;
			}
			Gson gson = new Gson();
			LoginResult result = gson.fromJson(new String(data),
					LoginResult.class);
			handlerResult(result);
			break;
		case HttpURLConnection.HTTP_UNAVAILABLE:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.startNetSetting(LoginActivity.this);
				}
			});
			dissmissDialog();
			break;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AuxiliaryUtils.toast(LoginActivity.this,
							R.string.msg_network_error);
					mDialog.cancel();
				}
			});
			dissmissDialog();
			break;
		}
	}
	
	private void handlerResult(final LoginResult result) {
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (result.isState()) {
					User tempUser = result.getInfo().get(0);
					if (null != tempUser) {
						user = new User();
						user = tempUser;
						user.setPWD(pw);
						user.setNAME(id);
						
						UserManager.saveUserInfo(LoginActivity.this, user);
						UserManager.saveRemeberPwd(LoginActivity.this,
								mCheckBox.isChecked());
						UserManager.saveLogout(LoginActivity.this, false);
					}
					
					dissmissDialog();
					if(user.getDISABLED() == 1) {//disable = 1 not login
						DialogUtil.dialogMessage(LoginActivity.this, "提示", "您的使用权限已到期，续订请编辑短信a到10658565，包月套餐5元/月", null, null, null, null);
					} else {
						goLogin();
						AuxiliaryUtils.toast(LoginActivity.this, "登录成功");
						LoginActivity.this.finish();
					}
					
				} else {
					AuxiliaryUtils.toast(LoginActivity.this, result.getMsg());
					dissmissDialog();
				}
			}
		});
	}

	private void dissmissDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.cancel();
				}
			}
		});
	}
	
	/**
	 * 登陆
	 */
	private void goLogin() {
		Intent intent = new Intent(LoginActivity.this,MainActivity.class);
		intent.putExtra("pw", pw);
		intent.putExtra("id", id);
		CurrentUserBean.getInstance().password = pw;
		CurrentUserBean.getInstance().userID = id;
		startActivity(intent);
	}
}
