package com.forboss.news;

import com.forboss.R;
import com.forboss.util.ForBossUtils;

import android.app.Activity;
import android.os.Bundle;

public class FlippingArticleListByCategoryActivity extends Activity {

	private String categoryId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flipping_article_list_by_category);
		categoryId = (String) ForBossUtils.getBundleData("category_id");
		
		
	}

}
