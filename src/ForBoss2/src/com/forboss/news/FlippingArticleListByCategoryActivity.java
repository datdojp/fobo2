package com.forboss.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.data.model.ArticleGroup;
import com.forboss.util.ForBossUtils;

public class FlippingArticleListByCategoryActivity extends Activity {
	private int categoryId;
	private FlipViewController flipViewController;
	private ArticleGroupAdapter flipViewControllerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flipping_article_list_by_category);
		categoryId = (Integer) ForBossUtils.getBundleData("category_id");

		ImageView imgCategoryText = (ImageView) findViewById(R.id.imgCategoryText);
		imgCategoryText.setImageBitmap(ForBossUtils.loadBitmapFromAssets(ForBossUtils.getConfig(Integer.toString(categoryId)), this));
		
		ImageButton buttonOption = (ImageButton) findViewById(R.id.buttonOption);
		buttonOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
			}
		});
		
		initSubcategoriesLayout();
		
		flipViewController = (FlipViewController) findViewById(R.id.flipViewController);
		flipViewControllerAdapter = new ArticleGroupAdapter(new ArrayList<ArticleGroup>());
		flipViewController.setAdapter(flipViewControllerAdapter);

		final boolean needToDisplayProgressAlert = Article.count(this, categoryId, 0) < 3;
		if (needToDisplayProgressAlert) {
			ForBossUtils.alertProgress(this, "Đang tải dữ liệu...");
		} else {
			updateFlipperAdapter();
		}
		APIHelper.getInstance().getArticles(categoryId, this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (needToDisplayProgressAlert) ForBossUtils.dismissProgress(getContext());
				updateFlipperAdapter();
			}
		});
	}

	private void initSubcategoriesLayout() {
		RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.subcategories_selecting, null);
		layout.setVisibility(View.INVISIBLE);
		
	}
	
	private void displaySubCategoriesLayout() {
		
	}
	
	private void updateFlipperAdapter() {
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				List<ArticleGroup> list = ArticleGroup.loadArticleGroups(getContext(), categoryId, 0);
				flipViewControllerAdapter.getData().clear();
				flipViewControllerAdapter.getData().addAll(list);
				flipViewControllerAdapter.notifyDataSetChanged();
				
				// load images
				APIHelper.getInstance().getImages(list, getContext(), new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Article article = (Article) msg.obj;
						boolean needUpdate = false;
						for (int i = 0; i < flipViewController.getChildCount(); i++) {
							View page = flipViewController.getChildAt(i);
							ArticleGroup articleGroup = (ArticleGroup) page.getTag();
							if (articleGroup != null && articleGroup.contains(article)) {
								needUpdate = true;
								break;
							}
						}
						if (needUpdate) flipViewControllerAdapter.notifyDataSetChanged(); 
					};
				}, null);
			};
		}.sendEmptyMessage(0);
	}

	protected class ArticleGroupAdapter extends BaseAdapter {
		private List<ArticleGroup> data;
		public List<ArticleGroup> getData() {
			return data;
		}
		public void setData(List<ArticleGroup> data) {
			this.data = data;
		}

		public ArticleGroupAdapter(List<ArticleGroup> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			if (data == null) return 0;
			return data.size();
		}

		@Override
		public Object getItem(int index) {
			if (data == null || index >= data.size()) return null;
			return data.get(index);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View view, ViewGroup container) {
			// create new view if needed
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.article_overview_group,	null); 
			}

			// get article group
			ArticleGroup articleGroup = data.get(pos);

			// apply content to view
			View viewTop = view.findViewById(R.id.topArticle);
			View viewLeft = view.findViewById(R.id.leftArticle);
			View viewRight = view.findViewById(R.id.rightArticle);
			if (articleGroup.top != null) {
				applyArticleToView(articleGroup.top, viewTop, true);
			} else {
				viewTop.setVisibility(View.INVISIBLE);
			}
			if (articleGroup.left != null) {
				applyArticleToView(articleGroup.left, viewLeft, false);
			} else {
				viewLeft.setVisibility(View.INVISIBLE);
			}
			if (articleGroup.right != null) {
				applyArticleToView(articleGroup.right, viewRight, false);
			} else {
				viewRight.setVisibility(View.INVISIBLE);
			}

			// view is ready to use
			view.setTag(articleGroup);
			return view;
		}

		private View applyArticleToView(Article article, View view, boolean isBigArticle) {
			view.setVisibility(View.VISIBLE);
			
			ImageView imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
			TextView textTitle = (TextView) view.findViewById(R.id.textTitle);
			if (isBigArticle) {
				textTitle.setTextAppearance(getContext(), R.style.article_overview_title_big);
			} else {
				textTitle.setTextAppearance(getContext(), R.style.article_overview_title_small);
			}

			// recycle thumbnail if needed
			ForBossUtils.recycleBitmapOfImage(imgThumbnail, getContext().getClass().getName());

			// set thumbnail if it is downloaded to internal storage
			if (article.getPictureLocation() != null) {
				Bitmap bm = ForBossUtils.loadBitmapFromInternalStorage(article.getPictureLocation(), (ContextWrapper) getContext());
				imgThumbnail.setImageBitmap(bm);
				imgThumbnail.setTag(bm);
			}

			// set title and body
			textTitle.setText(article.getTitle());
			
			// tap on view, navigate to detail
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					navToArticleDetail((Article)view.getTag());
				}
			});

			// return applied view
			view.setTag(article);
			return view;
		}
	}

	private void navToArticleDetail(Article article) {
		ForBossUtils.putBundleData("article", article);
		startActivity(new Intent(this, FlippingArticleDetailActivity.class));
	}
	
	private Context getContext() {
		return this;
	}
}
