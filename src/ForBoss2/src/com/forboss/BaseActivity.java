package com.forboss;

import com.forboss.util.ForBossUtils;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ForBossUtils.App.init(this);
	}
	
}
