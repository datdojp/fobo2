package com.forboss.news;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aphidmobile.flip.FlipViewController;
import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.data.model.Category;
import com.forboss.util.ForBossUtils;

public class FlippingArticleDetailActivity extends Activity {
	private FlipViewController flipViewController;
	private FlipViewControllerAdapter flipViewControllerAdapter;
	private ViewGroup root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root = (ViewGroup) getLayoutInflater().inflate(R.layout.flipping_article_detail, null);
		setContentView(root);

		// get current article
		Article article = (Article) ForBossUtils.getBundleData("article");
		ForBossUtils.putBundleData("article", null);

		// get list of article of the same category
		List<Article> listArticles = (List<Article>) ForBossUtils.getBundleData("list_articles");
		ForBossUtils.putBundleData("list_articles", null);

		// get category
		Category category = (Category) ForBossUtils.getBundleData("category");
		ForBossUtils.putBundleData("category", null);

		// set category text
		ImageView imgCategoryText = (ImageView) findViewById(R.id.imgCategoryText);
		imgCategoryText.setImageBitmap(category.getTextBitmap(this));

		// hide option button
		ImageButton buttonOption = (ImageButton) findViewById(R.id.buttonOption);
		buttonOption.setVisibility(View.INVISIBLE);

		// flipper
		flipViewController = new FlipViewController(this, FlipViewController.HORIZONTAL) {
			private float lastX, lastY;
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					lastX = event.getX();
					lastY = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {
					float dX = event.getX() - lastX;
					float dY = event.getY() - lastY;
					if (Math.abs(dX/dY) < 0.25) {
						return false;
					}
				}
				
				return super.onTouchEvent(event);
			}
		};
		RelativeLayout.LayoutParams lpOfFlipViewController =	new RelativeLayout.LayoutParams(
					    											RelativeLayout.LayoutParams.MATCH_PARENT,
					    											RelativeLayout.LayoutParams.MATCH_PARENT);
		lpOfFlipViewController.addRule(RelativeLayout.ABOVE, R.id.layoutLikeAndShare);
		lpOfFlipViewController.addRule(RelativeLayout.BELOW, R.id.layoutTopPanel);
    	flipViewController.setLayoutParams(lpOfFlipViewController);
    	root.addView(flipViewController);
		flipViewControllerAdapter = new FlipViewControllerAdapter(listArticles);
		flipViewController.setAdapter(flipViewControllerAdapter);
	}

	private class FlipViewControllerAdapter extends BaseAdapter {
		private List<Article> data;

		public FlipViewControllerAdapter(List<Article> data) {
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
			return i;
		}

		@Override
		public View getView(int pos, View view, ViewGroup container) {
			// get article
			Article article = data.get(pos);

			// init webview if needed
			WebView htmlContent = (WebView) view;
			if (htmlContent == null) {
				htmlContent = new WebView(getContext());
				htmlContent.setLayoutParams(	new AbsListView.LayoutParams(
													AbsListView.LayoutParams.MATCH_PARENT, 
													AbsListView.LayoutParams.MATCH_PARENT)	);
				htmlContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				htmlContent.getSettings().setLoadWithOverviewMode(true);
				htmlContent.getSettings().setDefaultFontSize(14);
				htmlContent.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
			}

			// set webview content
			String html = "<h3>Đang tải dữ liệu....</h3>";
			if (article.getHtmlContent() != null) {
				html = article.getHtmlContent();
			} else {
				loadArticleHtmlContentFromServer(article);
			}
			
			htmlContent.loadDataWithBaseURL(
					null,
					"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
							+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
							+ "<head>"
							+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
							+ "</head>"
							+ "<body style='background-color:black; color: white;'>"
							+ html
							+ "</body>"
							+ "</html>",
							"text/html", "utf-8", null);

			// return
			htmlContent.setTag(article);
			return htmlContent;
		}
	}

	private void loadArticleHtmlContentFromServer(Article article) {
		APIHelper.getInstance().getArticleDetail(article, getContext(), new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Object[] objs = (Object[]) msg.obj;
				Article article = (Article) objs[0];
				boolean needRefresh = (Boolean) objs[1];
				if (needRefresh) {
					for (int i = 0; i < flipViewController.getChildCount(); i++) {
						View view = flipViewController.getChildAt(i);
						Article viewArticle = (Article) view.getTag();
						if (article != null && viewArticle != null && viewArticle.getId() == article.getId()) {
							flipViewControllerAdapter.notifyDataSetChanged();
						}
					}
				}
				
			}
		});
	}
	
	
	private Context getContext() {
		return this;
	}
}
