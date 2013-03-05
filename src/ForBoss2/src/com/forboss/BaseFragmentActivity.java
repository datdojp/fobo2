package com.forboss;

import com.forboss.util.ForBossUtils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ForBossUtils.App.init(this);
	}
}
