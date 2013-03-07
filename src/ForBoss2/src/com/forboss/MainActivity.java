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

import com.tradedoubler.sdk.MobileSDK;

public class MainActivity extends Activity {

	private MainActivity getContext() {
		return this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Start of App Download Tracking
		// Values for the trackback... you should have received them from Tradedoubler
		String orgId = "1791723";
		String event = "276896";
		String secretCode = "699544286"; // Validate the trackback with this in our side String currency = "EUR";
		String orderValue = "1.49";
		boolean testMode = false; // Never set this to true in an app that goes in to production int timeout = 5; // Timeout in seconds
		int lifeTimeValue = 10; // For how many days should we track
		boolean trackAsSale = true; // Only used in this example
		MobileSDK mobileSDK = MobileSDK.getInstance(this);
		if(trackAsSale) { mobileSDK.trackDownloadAsSale(
		orgId, event, secretCode, "USD", orderValue, testMode, 5, lifeTimeValue );
		} else { mobileSDK.trackDownloadAsLead(
		orgId, event, secretCode, testMode, 5, lifeTimeValue );
		}
		// End of App Download Tracking
		
		Boolean shouldCloseApp = (Boolean) ForBossUtils.getBundleData("shouldCloseApp");
		if (shouldCloseApp != null && shouldCloseApp.booleanValue()) {
			ForBossUtils.putBundleData("shouldCloseApp", false);
			finish();
			return;
		}
		
		CommonData.getInstance().load(getContext());
		final boolean needToGetCategoriesFromServer = CommonData.getInstance().getAllCategories(this) == null 
														|| CommonData.getInstance().getAllCategories(this).size() == 0;
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
