package com.forboss;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.forboss.data.api.APIHelper;
import com.forboss.data.model.CommonData;
import com.forboss.util.ForBossUtils;

public class MainActivity extends Activity {

	private MainActivity getContext() {
		return this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Boolean shouldCloseApp = (Boolean) ForBossUtils.getBundleData("shouldCloseApp");
		if (shouldCloseApp != null && shouldCloseApp.booleanValue()) {
			ForBossUtils.putBundleData("shouldCloseApp", false);
			finish();
			return;
		}
		
		CommonData.getInstance().load(getContext());
		final boolean needToGetCategoriesFromServer = CommonData.getInstance().getAllCategories() == null 
														|| CommonData.getInstance().getAllCategories().size() == 0;
		if (needToGetCategoriesFromServer) {
			ForBossUtils.alertProgress(this, getResources().getString(R.string.loading_data));
			APIHelper.getInstance().getCategories(this, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ForBossUtils.dismissProgress(getContext());
					CommonData.getInstance().load(getContext());
					navToFirstScreen();
				}
			});
		} else {
			CommonData.getInstance().load(getContext());
			navToFirstScreen();
		}
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
