package com.forboss.bossmeasure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.forboss.R;

public class BossMeasureActivity extends FragmentActivity {
	FragmentManager fragmentManager;
	ViewGroup layoutFragmentContainer;
	
	private static final int INTRO_FRAGMENT = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boss_measure);
		
		fragmentManager = getSupportFragmentManager();
		layoutFragmentContainer = (ViewGroup) findViewById(R.id.layoutFragmentContainer);
	}
	
	private void navToFragment(int n) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Fragment fragment = null;
		if (n == INTRO_FRAGMENT) {
			fragment = new BossMeasureIntroFrament();
		} else {
			
		}
		transaction.add(R.id.layoutFragmentContainer, fragment);
		transaction.commit();
		
	}
	
	
	
}
