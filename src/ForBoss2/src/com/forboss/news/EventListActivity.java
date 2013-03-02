package com.forboss.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forboss.ForBossApplication;
import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.data.model.Category;
import com.forboss.data.model.CommonData;
import com.forboss.util.ForBossUtils;

public class EventListActivity extends Activity {
	private ListView layoutEventList;
	private EventListAdapter layoutEventListAdapter;
	private static final int categoryId = Category.CATEGORY_EVENT_ID;
	private Category category;
	private AsyncTask articleGettingTask;
	private AsyncTask imageLoadingTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);

		// category
		category = CommonData.getInstance().getCategory(categoryId);
		
		// list view
		layoutEventList = (ListView) findViewById(R.id.layoutEventList);
		layoutEventListAdapter = new EventListAdapter(new ArrayList<Article>());
		layoutEventList.setAdapter(layoutEventListAdapter);

		// category text
		ImageView imgCategoryText = (ImageView) findViewById(R.id.imgCategoryText);
		imgCategoryText.setImageBitmap(category.getTextBitmap(this));
		
		// button option
		ImageButton buttonOption = (ImageButton) findViewById(R.id.buttonOption);
		buttonOption.setVisibility(View.INVISIBLE);
		
		// button back
		ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		ForBossUtils.UI.initHomeButton(this);
	}

	private void loadData() {
		final boolean needToDisplayProgressAlert = Article.count(this, category.getQueryCategoryId(), category.getQuerySubcategoryId()) == 0;
		if (needToDisplayProgressAlert) {
			ForBossUtils.alertProgress(this, getResources().getString(R.string.loading_data));
		} else {
			updateEventListAdapter();
		}
		articleGettingTask = APIHelper.getInstance().getArticles(category.getId(), this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (needToDisplayProgressAlert) ForBossUtils.dismissProgress(getContext());
				Boolean temp = (Boolean) msg.obj;
				boolean needRefresh = temp != null && temp.booleanValue();
				if (needRefresh) updateEventListAdapter();
			}
		});
	}
	
	private void updateEventListAdapter() {
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (imageLoadingTask != null) {
					imageLoadingTask.cancel(true);
					imageLoadingTask = null;
				}
				
				List<Article> list = Article.loadArticlesOrderedCreatedTimeDesc(getContext(), category.getQueryCategoryId(), category.getQuerySubcategoryId(), 0, Integer.MAX_VALUE);; 
				layoutEventListAdapter.getData().clear();
				layoutEventListAdapter.getData().addAll(list);
				if (layoutEventListAdapter.getData().size() != 0) {
					layoutEventListAdapter.notifyDataSetChanged();
				}

				// load images
				imageLoadingTask = APIHelper.getInstance().getImages(list, getContext(), new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Article article = (Article) msg.obj;
						boolean needUpdate = false;
						for (int i = 0; i < layoutEventList.getChildCount(); i++) {
							View item = layoutEventList.getChildAt(i);
							Article itemArticle = (Article) item.getTag();
							if (article != null && itemArticle != null && itemArticle.getId() == article.getId()) {
								needUpdate = true;
								break;
							}
						}
						if (needUpdate) layoutEventListAdapter.notifyDataSetChanged(); 
					};
				}, null);
			};
		}.sendEmptyMessage(0);
	}

	private class EventListAdapter extends BaseAdapter {
		private List<Article> data;
		public List<Article> getData() {
			return data;
		}
		public void setData(List<Article> data) {
			this.data = data;
		}

		public EventListAdapter(List<Article> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			if (data != null) return data.size();
			return 0;
		}

		@Override
		public Object getItem(int i) {
			if (data != null && i < data.size()) return data.get(i);
			return null;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int pos, View view, ViewGroup container) {
			// create if needed
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.event_item,	null);
				view.setLayoutParams(	new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 
																	ForBossUtils.convertDpToPixel(70, getContext()))	);
			}

			// get article
			Article article = data.get(pos);

			// set thumbnail
			ImageView imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
			imgThumbnail.getLayoutParams().width = ForBossApplication.getWindowDisplay().getWidth() * 193 / 640;
			// recycle thumbnail if needed
			ForBossUtils.recycleBitmapOfImage(imgThumbnail, getContext().getClass().getName());
			// set thumbnail if it is downloaded to internal storage
			if (article.getPictureLocation() != null) {
				Bitmap bm = ForBossUtils.loadBitmapFromInternalStorage(article.getPictureLocation(), (ContextWrapper) getContext());
				imgThumbnail.setImageBitmap(bm);
				imgThumbnail.setTag(bm);
			}

			// set mask
			ImageView imgMask = (ImageView) view.findViewById(R.id.imgMask);
			if (pos % 2 == 0) {
				imgMask.setImageResource(R.drawable.bg_event_item_even);
			} else {
				imgMask.setImageResource(R.drawable.bg_event_item_odd);	
			}

			// set title
			TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
			((RelativeLayout.LayoutParams)txtTitle.getLayoutParams()).leftMargin = 
					imgThumbnail.getLayoutParams().width + ForBossUtils.convertDpToPixel(3, getContext());
			txtTitle.setText(article.getTitle());

			// click
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navToArticleDetail((Article) v.getTag());
				}
			});
			
			// return view
			view.setTag(article);
			return view;
		}

	}

	private void navToArticleDetail(Article article) {
		ForBossUtils.putBundleData("article", article);
		ForBossUtils.putBundleData("list_articles", layoutEventListAdapter.getData());
		ForBossUtils.putBundleData("category", category);
		startActivity(new Intent(this, FlippingArticleDetailActivity.class));
	}
	
	private Context getContext() {
		return this;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAllTasks();
		deallocateAllBitmaps();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopAllTasks();
		deallocateAllBitmaps();
	}
	
	private void stopAllTasks() {
		if (articleGettingTask != null) {
			articleGettingTask.cancel(true);
			articleGettingTask = null;
		}
		
		if (imageLoadingTask != null) {
			imageLoadingTask.cancel(true);
			imageLoadingTask = null;
		}
	}
	
	private void deallocateAllBitmaps() {
		for (int i = 0; i < layoutEventList.getChildCount(); i++) {
			View view = layoutEventList.getChildAt(i);
			ImageView imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
			ForBossUtils.recycleBitmapOfImage(imgThumbnail, getContext().getClass().getName());
		}
		System.gc();
	}
}
