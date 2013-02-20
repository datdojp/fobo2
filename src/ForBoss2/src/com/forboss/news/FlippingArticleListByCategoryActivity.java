package com.forboss.news;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.data.model.ArticleGroup;
import com.forboss.util.ForBossUtils;

public class FlippingArticleListByCategoryActivity extends Activity {
	private String categoryId;
	private FlipViewController flipViewController;
	private ArticleGroupAdapter flipViewControllerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flipping_article_list_by_category);
		categoryId = (String) ForBossUtils.getBundleData("category_id");



		// load article from server
		ForBossUtils.alertProgress(this, "Đang tải dữ liệu...");
		APIHelper.getInstance().getArticles(categoryId, this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ForBossUtils.dismissProgress(getContext());
				initFlipper();
			}
		});
	}

	private void initFlipper() {
		// init flipper
		flipViewController = (FlipViewController) findViewById(R.id.flipViewController);
		try {
			flipViewControllerAdapter = new ArticleGroupAdapter(ArticleGroup.loadArticleGroups(this, 0, 0));
		} catch (SQLException e) {
			Log.e(this.getClass().getName(), "Unable to load all articles from db", e);
		}
		flipViewController.setAdapter(flipViewControllerAdapter);
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
			if (articleGroup.top != null)
				applyArticleToView(articleGroup.top, view.findViewById(R.id.topArticle));
			if (articleGroup.left != null)
				applyArticleToView(articleGroup.left, view.findViewById(R.id.leftArticle));
			if (articleGroup.right != null)
				applyArticleToView(articleGroup.right, view.findViewById(R.id.rightArticle));

			// view is ready to use
			return view;
		}

		private View applyArticleToView(Article article, View view) {
			ImageView imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
			TextView textTitle = (TextView) view.findViewById(R.id.textTitle);
			TextView textBody = (TextView) view.findViewById(R.id.textBody);

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
			textBody.setText(article.getBody());

			// return applied view
			return view;
		}
	}

	private Context getContext() {
		return this;
	}
}
