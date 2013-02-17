package com.forboss;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.forboss.util.ForBossUtils;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navToFirstScreen();
	}

	private void navToFirstScreen() {
		SharedPreferences pref = ForBossUtils.Storage.getSharedPreferences(this);
		Class clazz;
		if (pref.getBoolean(ForBossUtils.Storage.SHARED_PREFERENCES_KEY_REGISTERED, false)) {
			clazz = FeatureSelectingActivity.class;
		} else {
			clazz = RegistrationActivity.class;
		}
		startActivity(new Intent(this, clazz));
	}
	
}
