package com.forboss.news;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	private WebView web;

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

		// button back
		ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// hide option button
		ImageButton buttonOption = (ImageButton) findViewById(R.id.buttonOption);
		buttonOption.setVisibility(View.INVISIBLE);

		// web view to display html content
		web = new WebView(getContext());
		web.setLayoutParams(	new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 
				RelativeLayout.LayoutParams.MATCH_PARENT)	);
		web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setDefaultFontSize(14);
		web.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		web.setBackgroundColor(Color.BLACK);

		flipViewController = new FlipViewController(this, FlipViewController.HORIZONTAL);
		flipViewController.setTouchSlop(flipViewController.getTouchSlop() * 10);
		flipViewController.setOnViewFlipListener(new FlipViewController.ViewFlipListener() {
			@Override
			public void onViewFlipped(View view, int pos) {
				displayHtmlContent(view);
			}
		});
		RelativeLayout.LayoutParams lpOfFlipViewController =	new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lpOfFlipViewController.addRule(RelativeLayout.ABOVE, R.id.layoutLikeAndShare);
		lpOfFlipViewController.addRule(RelativeLayout.BELOW, R.id.layoutTopPanel);
		flipViewController.setLayoutParams(lpOfFlipViewController);
		root.addView(flipViewController);
		flipViewControllerAdapter = new FlipViewControllerAdapter(listArticles);
		flipViewController.setAdapter(flipViewControllerAdapter);
		flipViewController.setSelection(listArticles.indexOf(article));
		displayHtmlContent(flipViewController.getSelectedView());
	}

	private void displayHtmlContent(View view) {
		Article article = (Article) view.getTag();
		if (article == null) return;
		String html = "<h3>Đang tải dữ liệu....</h3>";
		if (article.getHtmlContent() != null) {
			html = article.getHtmlContent();
		} else {
			loadArticleHtmlContentFromServer(article);
		}

		web.clearView();
		ForBossUtils.removeView((ViewGroup) web.getParent(), web);
		ForBossUtils.addView((ViewGroup) view, web);
		web.loadDataWithBaseURL(
				null,
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
						+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
						+ "<head>"
						+ "<style>"
						+ "body, div, p, table, img, h1, h2, h3, h4, tr, td {"
						+ "max-width: 320px !important;"
						+ "background-color: #000 !important;" 
						+ "color: #fff !important;"
						+ "font-family: Arial !important;"
						+ "}"
						+ "img {height: auto !important}"
						+ "</style>"
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
						+ "</head>"
						+ "<body>"
						+ html
						+ "</body>"
						+ "</html>",
						"text/html", "utf-8", null);
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
		public View getView(final int pos, View view, ViewGroup container) {
			Article article = data.get(pos);
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.article_detail_item, null);
			}

			TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
			txtTitle.setText(article.getTitle());

			view.setTag(article);
			return view;
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
					View view = flipViewController.getSelectedView();
					Article viewArticle = (Article) view.getTag();
					if (article != null && viewArticle != null && viewArticle.getId() == article.getId()) {
						displayHtmlContent(view);
					}
				}

			}
		});
	}


	private Context getContext() {
		return this;
	}
}
