package com.forboss.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.forboss.R;
import com.forboss.data.model.Category;
import com.forboss.data.model.CommonData;
import com.forboss.util.ForBossUtils;

public class CategorySelectingActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_selecting);
		
		((ImageButton)findViewById(R.id.buttonAttraction)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(Category.CATEGORY_ATTENTION_ID);
			}
		});
		
		((ImageButton)findViewById(R.id.buttonLevel)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(Category.CATEGORY_LEVEL_ID);
			}
		});
		
		((ImageButton)findViewById(R.id.buttonStyle)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(Category.CATEGORY_STYLE_ID);
			}
		});
		
		((ImageButton)findViewById(R.id.buttonSuccess)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToCategory(Category.CATEGORY_SUCCESS_ID);
			}
		});
		
		// button back
		ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		// button event
		ImageButton buttonEvent = (ImageButton) findViewById(R.id.buttonEvent);
		buttonEvent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				navToEvents();
			}
		});
	}
	
	private void navToEvents() {
		startActivity(new Intent(this, EventListActivity.class));
	}

	private void navToCategory(int categoryId) {
		ForBossUtils.putBundleData("category", CommonData.getInstance().getCategory(categoryId));
		startActivity(new Intent(this, FlippingArticleListByCategoryActivity.class));
	}
	
}
