package com.forboss;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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
		editPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
		editCMND = (EditText) findViewById(R.id.editCMND);
		
//		handleRegistrationSuccess();//TODO: remove this line
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
				handleRegistrationSuccess();
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
}
