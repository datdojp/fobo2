package com.forboss.news;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.MeasureSpec;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;

import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.util.ForBossUtils;

public class FlippingArticleDetailActivity extends Activity {
	private Article article;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flipping_article_detail);
		article = (Article) ForBossUtils.getBundleData("article");

		final boolean needToDisplayProgressAlert = article.getHtmlContent() == null;
		if (needToDisplayProgressAlert) {
			ForBossUtils.alertProgress(this, "Đang lấy nội dung...");
		} else {
			displayContent();
		}
		APIHelper.getInstance().getArticleDetail(article, this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (needToDisplayProgressAlert) ForBossUtils.dismissProgress(getContext());
				if (msg.obj != null) {
					Object[] objs = (Object[]) msg.obj;
					boolean needRefresh = ((Boolean)objs[1]).booleanValue(); 
					if (needRefresh) displayContent();
				}
			}
		});
	}

	private void displayContent() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				final WebView htmlContent = (WebView) findViewById(R.id.web);
				htmlContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				htmlContent.getSettings().setLoadWithOverviewMode(true);
				htmlContent.getSettings().setDefaultFontSize(14);
				htmlContent.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
				htmlContent.getSettings().setBuiltInZoomControls(true);
				htmlContent.getSettings().setSupportZoom(true);
				htmlContent
				.loadDataWithBaseURL(
						null,
						"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
								+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
								+ "<head>"
								+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
								+ "</head>"
								+ "<body style='background-color:black; color: white;'>"
								+ article.getHtmlContent()
								+ "</body>"
								+ "</html>",
								"text/html", "utf-8", null);

				htmlContent.setDrawingCacheEnabled(true);
				htmlContent.measure(	MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)	);
				htmlContent.layout(0, 0, htmlContent.getMeasuredWidth(), htmlContent.getMeasuredHeight());
				htmlContent.buildDrawingCache(true);
				Bitmap bitmap = Bitmap.createBitmap(htmlContent.getDrawingCache());
				//				htmlContent.destroyDrawingCache();
				htmlContent.setDrawingCacheEnabled(false);

				ImageView img = (ImageView) findViewById(R.id.img);
				img.setImageBitmap(bitmap);
			}
		}, 2000);

	}

	private Context getContext() {
		return this;
	}
}
