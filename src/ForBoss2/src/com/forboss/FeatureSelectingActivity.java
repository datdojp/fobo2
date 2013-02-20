package com.forboss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.forboss.bossmeasure.BossMeasureActivity;
import com.forboss.news.CategorySelectingActivity;


public class FeatureSelectingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feature_selecting);
		
		ImageButton buttonBossMeasure = (ImageButton) findViewById(R.id.buttonBossMeasure);
		buttonBossMeasure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				navToBossMeasure();
			}
		});
		
		ImageButton buttonNews = (ImageButton) findViewById(R.id.buttonNews);
		buttonNews.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToNews();
			}
		});
		
	}
	
	private void navToBossMeasure() {
		startActivity(new Intent(this, BossMeasureActivity.class));
	}
	
	private void navToNews() {
		startActivity(new Intent(this, CategorySelectingActivity.class));
	}

}
