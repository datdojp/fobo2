package com.forboss.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.forboss.R;
import com.forboss.util.ForBossUtils;

public class CategorySelectingActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_selecting);
		
		((ImageButton)findViewById(R.id.buttonAttraction)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(ForBossUtils.getConfig("CATEGORY_ATTENTION_ID"));
			}
		});
		
		((ImageButton)findViewById(R.id.buttonLevel)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(ForBossUtils.getConfig("CATEGORY_LEVEL_ID"));
			}
		});
		
		((ImageButton)findViewById(R.id.buttonStyle)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(ForBossUtils.getConfig("CATEGORY_STYLE_ID"));				
			}
		});
		
		((ImageButton)findViewById(R.id.buttonSuccess)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(ForBossUtils.getConfig("CATEGORY_SUCCESS_ID"));
			}
		});
	}

	private void navToCategory(String categoryId) {
		ForBossUtils.putBundleData("category_id", categoryId);
		startActivity(new Intent(this, FlippingArticleListByCategoryActivity.class));
	}
	
}
