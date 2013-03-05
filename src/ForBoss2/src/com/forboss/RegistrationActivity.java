package com.forboss;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.forboss.data.api.APIClient;
import com.forboss.util.ForBossUtils;

public class RegistrationActivity extends Activity {

	private ImageButton buttonSubmit;
	private EditText editEmail, editPhoneNumber, editCMND;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);

		buttonSubmit = (ImageButton) findViewById(R.id.buttonSubmit);
		buttonSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		});

		editEmail = (EditText) findViewById(R.id.editEmail);
		editEmail.setTextColor(Color.WHITE);
		editPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
		editPhoneNumber.setTextColor(Color.WHITE);
		editCMND = (EditText) findViewById(R.id.editCMND);
		editCMND.setTextColor(Color.WHITE);
		editCMND.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				submit();
				return true;
			}
		});
	}

	private void submit() {
		String email = editEmail.getText().toString().trim();
		String phoneNumber = editPhoneNumber.getText().toString().trim();
		String cmnd = editCMND.getText().toString().trim();
		if (!ForBossUtils.Validation.isValidEmailAddress(email)) {
			ForBossUtils.alert(this, "Email không hợp lệ");
			editEmail.requestFocus();
			return;
		}
		if (!ForBossUtils.Validation.isValidPhoneNumber(phoneNumber)) {
			ForBossUtils.alert(this, "Số điện thoại không hợp lệ");
			editPhoneNumber.requestFocus();
			return;
		}
		if (!ForBossUtils.Validation.isValidCMND(cmnd)) {
			ForBossUtils.alert(this, "CMND không hợp lệ");
			editCMND.requestFocus();
			return;
		}
		APIClient.getClient().signup(email, phoneNumber, cmnd, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String jsonString = (String) msg.obj;
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					if (jsonObject.getBoolean("success")) {
						handleRegistrationSuccess();	
					} else {
						int errorcode = jsonObject.getInt("errorcode");
						String errMsg = "";
						if (errorcode == 1) {
							errMsg = "Bạn chưa nhập đầy đủ các thông tin. Vui lòng thử lại.";
						} else if (errorcode == 2) {
							errMsg = "Địa chỉ email này đã được sử dụng.";
						} else if (errorcode == 3) {
							errMsg = "Số điện thoại này đã được sử dụng.";
						} else if (errorcode == 4) {
							errMsg = "Số CMND này đã được sử dụng.";
						} else {
							errMsg = "Có lỗi trong quá trình kết nối mạng.";
						}
						ForBossUtils.alert(getContext(), errMsg);
					}
				} catch (JSONException e) {
					Log.e(RegistrationActivity.class.getName(), "Unable to parse json data", e);
				}
			}
		},
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				handleRegistrationFailed();
			}
		});
	}
	
	private void handleRegistrationSuccess() {
		SharedPreferences pref = ForBossUtils.Storage.getSharedPreferences(this);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(ForBossUtils.Storage.SHARED_PREFERENCES_KEY_REGISTERED, true);
		editor.commit();
		startActivity(new Intent(this, FeatureSelectingActivity.class));
	}
	
	private void handleRegistrationFailed() {
		ForBossUtils.alert(this, "Đăng ký thất bại. Máy chủ bị lỗi hoặc bạn chưa bật kết nối mạng");
	}
	
	@Override
	public void onBackPressed() {
		ForBossUtils.UI.closeApp(this);
	}
	
	private Context getContext() {
		return this;
	}
}
