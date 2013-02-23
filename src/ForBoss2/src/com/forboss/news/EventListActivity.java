package com.forboss.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.util.ForBossUtils;

public class EventListActivity extends Activity {
	private ListView layoutEventList;
	private EventListAdapter layoutEventListAdapter;
	private static final int categoryId = Integer.parseInt(ForBossUtils.getConfig("CATEGORY_EVENT_ID")); 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);

		// list view
		layoutEventList = (ListView) findViewById(R.id.layoutEventList);
		layoutEventListAdapter = new EventListAdapter(new ArrayList<Article>());
		layoutEventList.setAdapter(layoutEventListAdapter);

		// load data
		final boolean needToDisplayProgressAlert = Article.count(this, categoryId, 0) == 0;
		if (needToDisplayProgressAlert) {
			ForBossUtils.alertProgress(this, "Đang tải dữ liệu...");
		} else {
			updateEventListAdapter();
		}
		APIHelper.getInstance().getArticles(categoryId, this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (needToDisplayProgressAlert) ForBossUtils.dismissProgress(getContext());
				updateEventListAdapter();
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
	}

	private void updateEventListAdapter() {
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				List<Article> list = Article.loadArticlesOrderedCreatedTimeDesc(getContext(), categoryId, 0);
				layoutEventListAdapter.getData().clear();
				layoutEventListAdapter.getData().addAll(list);
				if (layoutEventListAdapter.getData().size() != 0) {
					layoutEventListAdapter.notifyDataSetChanged();
				}

				// load images
				APIHelper.getInstance().getImages(list, getContext(), new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Article article = (Article) msg.obj;
						boolean needUpdate = false;
						for (int i = 0; i < layoutEventList.getChildCount(); i++) {
							View item = layoutEventList.getChildAt(i);
							Article itemArticle = (Article) item.getTag();
							if (itemArticle.getId() == article.getId()) {
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
			if (data != null) return data.get(i);
			return null;
		}

		@Override
		public long getItemId(int i) {
			return 0;
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
			txtTitle.setText(article.getTitle());

			// return view
			view.setTag(article);
			return view;
		}

	}

	private Context getContext() {
		return this;
	}
}
