package com.forboss.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
import com.forboss.ForBossApplication;
import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.data.model.ArticleGroup;
import com.forboss.data.model.Category;
import com.forboss.data.model.CommonData;
import com.forboss.util.ForBossUtils;

public class FlippingArticleListByCategoryActivity extends Activity {
	private Category category;
	private FlipViewController flipViewController;
	private ArticleGroupAdapter flipViewControllerAdapter;
	private ViewGroup root;
	private RelativeLayout layoutSubcategriesSelecting;
	private LinearLayout layoutPagingIndicatorImages;
	private static final int SUBCAT_SELECTIONG_ANIMATION_DURATIO = 300;
	private AsyncTask articleGettingTask;
	private Stack<AsyncTask> imageLoadingTasks = new Stack<AsyncTask>();
	private ImageButton buttonClose;
	
	private static final int N_ARTICLES_TO_LOAD = 9;
	private int nLoadedArticle = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root = (ViewGroup) getLayoutInflater().inflate(R.layout.flipping_article_list_by_category, null);
		setContentView(root);
		category = (Category) ForBossUtils.getBundleData("category");
		ForBossUtils.putBundleData("category", null);

		// category text
		ImageView imgCategoryText = (ImageView) findViewById(R.id.imgCategoryText);
		imgCategoryText.setImageBitmap(category.getTextBitmap(this));

		// button option
		ImageButton buttonOption = (ImageButton) findViewById(R.id.buttonOption);
		buttonOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showSubCategoriesSelecting();
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

		// button refresh
		ImageButton buttonRefresh = (ImageButton) root.findViewById(R.id.buttonRefresh);
		buttonRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				refreshData();
			}
		});
		
		ForBossUtils.UI.initHomeButton(this);

		// layout for paging indicator images
		layoutPagingIndicatorImages = (LinearLayout) root.findViewById(R.id.layoutPagingIndicatorImages);

		// layout to select subcategory
		initSubcategoriesSelecting();

		// flipper
		flipViewController = (FlipViewController) findViewById(R.id.flipViewController);
		flipViewController.setOnViewFlipListener(new FlipViewController.ViewFlipListener() {
			@Override
			public void onViewFlipped(View view, int position) {
				setPagingIndicatorPosition(position);
				if (position == flipViewControllerAdapter.getData().size() - 2 && !didReachTheLastArticle) {
					loadNextDataFromServer();
				}
			}
		});
		flipViewControllerAdapter = new ArticleGroupAdapter(new ArrayList<ArticleGroup>());
		flipViewController.setAdapter(flipViewControllerAdapter);
	}
	
	private boolean didReachTheLastArticle = false;
	private void loadNextDataFromServer() {
		if (!ForBossUtils.isNetworkAvailable(this)) {
			nLoadedArticle += N_ARTICLES_TO_LOAD;
			loadDataFromDatabase();
			return;
		}
		final boolean needToDisplayProgressAlert = flipViewControllerAdapter.getData() == null || flipViewControllerAdapter.getData().isEmpty();
		if (needToDisplayProgressAlert) {
			ForBossUtils.alertProgress(this, getResources().getString(R.string.loading_data));
		}
		articleGettingTask = APIHelper.getInstance().getArticles(category.getId(), nLoadedArticle + 1, N_ARTICLES_TO_LOAD, this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (needToDisplayProgressAlert) ForBossUtils.dismissProgress(getContext());
				int nReturnedArticles = (Integer) ((Object[])msg.obj)[1];
				nLoadedArticle += nReturnedArticles;
				if (nReturnedArticles < N_ARTICLES_TO_LOAD) didReachTheLastArticle = true;
				loadDataFromDatabase();
			}
		});
	}

	private void initSubcategoriesSelecting() {
		layoutSubcategriesSelecting = (RelativeLayout) findViewById(R.id.layoutSubcategriesSelecting);
		final LinearLayout layoutSubcategories = (LinearLayout) layoutSubcategriesSelecting.findViewById(R.id.layoutSubcategories);
		List<Category> listSubcategories = CommonData.getInstance().getSubcategories(category.getQueryCategoryId());
		for (final Category subcat : listSubcategories) {
			// add view for each sub-category
			View viewSubcat = getLayoutInflater().inflate(R.layout.subcategory, null);
			viewSubcat.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					ForBossUtils.convertDpToPixel(50, this)
					));

			ImageView imgIcon = (ImageView) viewSubcat.findViewById(R.id.imgIcon);
			Bitmap bm = subcat.getIconBitmap(getContext());
			imgIcon.getLayoutParams().height = imgIcon.getLayoutParams().width * bm.getHeight() / bm.getWidth(); 
			imgIcon.setImageBitmap(bm);

			TextView txtName = (TextView) viewSubcat.findViewById(R.id.txtName);
			txtName.setText(subcat.getTitle().toUpperCase(ForBossApplication.getDefaultLocale()));
			if (subcat.getId() == category.getQuerySubcategoryId()) {
				viewSubcat.setBackgroundColor(0xff262626);
			}
			layoutSubcategories.addView(viewSubcat);

			viewSubcat.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					hideSubCategoriesSelecting(new Handler() {
						@Override
						public void handleMessage(Message msg) {
							// navigate to next subcat
							ForBossUtils.putBundleData("category", subcat);
							startActivity(new Intent(getContext(), FlippingArticleListByCategoryActivity.class));
						}
					});
				}
			});

			// add deviders
			if (listSubcategories.indexOf(subcat) != listSubcategories.size() - 1) {
				View divider = new View(this);
				divider.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						ForBossUtils.convertDpToPixel(1, this)
						));
				divider.setBackgroundResource(R.drawable.subcat_layout_divider);
				layoutSubcategories.addView(divider);
			}

		}

		buttonClose = (ImageButton) layoutSubcategriesSelecting.findViewById(R.id.buttonClose);
		buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				hideSubCategoriesSelecting(null);
			}
		});

		layoutSubcategriesSelecting.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// set position of layoutSubcategories
				RelativeLayout.LayoutParams lpOfLayoutSubcategories = (LayoutParams) layoutSubcategories.getLayoutParams();
				lpOfLayoutSubcategories.leftMargin = (layoutSubcategriesSelecting.getWidth() - layoutSubcategories.getWidth())/2;
				lpOfLayoutSubcategories.topMargin = ForBossUtils.convertDpToPixel(30, getContext());
			}
		});
	}

	private void showSubCategoriesSelecting() {
		layoutSubcategriesSelecting.setVisibility(View.VISIBLE);
		flipViewController.setTouchEnable(false);
		Animation animation = new TranslateAnimation(0, 0, -layoutSubcategriesSelecting.getHeight(), 0);
		animation.setDuration(SUBCAT_SELECTIONG_ANIMATION_DURATIO);
		layoutSubcategriesSelecting.startAnimation(animation);
	}

	private void hideSubCategoriesSelecting(final Handler finishHandler) {
		Animation animation = new TranslateAnimation(0, 0, 0, -layoutSubcategriesSelecting.getHeight());
		animation.setDuration(SUBCAT_SELECTIONG_ANIMATION_DURATIO);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				layoutSubcategriesSelecting.setVisibility(View.INVISIBLE);
				flipViewController.setTouchEnable(true);
				if (finishHandler != null) finishHandler.sendEmptyMessage(0);
			}
		});
		layoutSubcategriesSelecting.startAnimation(animation);
	}

	private void loadDataFromDatabase() {
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				List<ArticleGroup> list = ArticleGroup.loadArticleGroups(getContext(), category.getQueryCategoryId(), category.getQuerySubcategoryId(),
																			0, nLoadedArticle);
				flipViewControllerAdapter.getData().clear();
				flipViewControllerAdapter.getData().addAll(list);
				if (flipViewControllerAdapter.getData().size() != 0) {
					flipViewControllerAdapter.notifyDataSetChanged();
				}

				// load images
				imageLoadingTasks.push(APIHelper.getInstance().getImages(list, getContext(), new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Article article = (Article) msg.obj;
						boolean needUpdate = false;
						for (int i = 0; i < flipViewController.getChildCount(); i++) {
							View page = flipViewController.getChildAt(i);
							ArticleGroup articleGroup = (ArticleGroup) page.getTag();
							if (article != null && articleGroup != null && articleGroup.contains(article)) {
								needUpdate = true;
								break;
							}
						}
						if (needUpdate) flipViewControllerAdapter.notifyDataSetChanged(); 
					};
				}, null));
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

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			initPagingIndicatorImages();
		}
	}

	private void navToArticleDetail(Article article) {
		ForBossUtils.putBundleData("article", article);
		ForBossUtils.putBundleData("list_articles", ArticleGroup.convertToArticles(flipViewControllerAdapter.getData()));
		ForBossUtils.putBundleData("category", category);
		startActivity(new Intent(this, FlippingArticleDetailActivity.class));
	}

	private Context getContext() {
		return this;
	}

	@Override
	public void onBackPressed() {
		if (layoutSubcategriesSelecting.getVisibility() == View.VISIBLE) {
			hideSubCategoriesSelecting(null);
		} else {
			super.onBackPressed();
		}
	}

	private void initPagingIndicatorImages() {
		layoutPagingIndicatorImages.removeAllViews();
		int width = ForBossUtils.convertDpToPixel(7, this);
		int height = width;
		int gap = ForBossApplication.getWindowDisplay().getWidth() - flipViewControllerAdapter.getCount() * width;
		gap = Math.max(0, gap);
		gap = Math.min(gap, 2 * width);
		for (int i = 0; i < flipViewControllerAdapter.getCount(); i++) {
			ImageView img = new ImageView(this);
			img.setImageResource(R.drawable.selected_page);
			img.setBackgroundColor(Color.TRANSPARENT);
			img.setScaleType(ScaleType.FIT_XY);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
			if (i != 0) lp.leftMargin = gap; 
			img.setLayoutParams(lp);

			layoutPagingIndicatorImages.addView(img);
		}

		setPagingIndicatorPosition(flipViewController.getSelectedItemPosition());
	}

	private void setPagingIndicatorPosition(int pos) {
		for (int i = 0; i < layoutPagingIndicatorImages.getChildCount(); i++) {
			ImageView img = (ImageView) layoutPagingIndicatorImages.getChildAt(i);
			if (i == pos) {
				img.setImageResource(R.drawable.selected_page);
			} else {
				img.setImageResource(R.drawable.unselected_page);
			}
		}
	}

	private void refreshData() {
		stopAllTasks();
		flipViewController.setSelection(0);
		nLoadedArticle = 0;
		didReachTheLastArticle = false;
		loadNextDataFromServer();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAllTasks();
		deallocateAllBitmaps();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopAllTasks();
		deallocateAllBitmaps();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (flipViewControllerAdapter.getData() != null && !flipViewControllerAdapter.getData().isEmpty()) {
			flipViewControllerAdapter.notifyDataSetChanged();
		} else {
			loadNextDataFromServer();
		}
	}
	
	private void stopAllTasks() {
		if (articleGettingTask != null) {
			articleGettingTask.cancel(true);
			articleGettingTask = null;
		}
		
		if (imageLoadingTasks != null) {
			while (!imageLoadingTasks.isEmpty()) {
				AsyncTask task = imageLoadingTasks.pop();
				if (task != null) task.cancel(true);
			}
		}
	}
	
	private void deallocateAllBitmaps() {
		for (int i = 0; i < flipViewController.getChildCount(); i++) {
			View view = flipViewController.getChildAt(i);
			View[] children = new View[] { 
					view.findViewById(R.id.topArticle),
					view.findViewById(R.id.leftArticle),
					view.findViewById(R.id.rightArticle)	};
			for (View child : children) {
				if (child == null) continue;
				ImageView imgThumbnail = (ImageView) child.findViewById(R.id.imgThumbnail);
				ForBossUtils.recycleBitmapOfImage((ImageView) imgThumbnail, getContext().getClass().getName());
			}
		}
		System.gc();
	}
}
