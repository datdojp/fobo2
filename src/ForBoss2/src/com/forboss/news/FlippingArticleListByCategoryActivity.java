package com.forboss.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.aphidmobile.flip.FlipViewController;
import com.forboss.R;
import com.forboss.data.api.APIHelper;
import com.forboss.data.model.Article;
import com.forboss.data.model.ArticleGroup;
import com.forboss.data.model.Category;
import com.forboss.data.model.CommonData;
import com.forboss.util.ForBossUtils;

public class FlippingArticleListByCategoryActivity extends Activity {
	private int categoryId;
	private int subcategoryId;
	private FlipViewController flipViewController;
	private ArticleGroupAdapter flipViewControllerAdapter;
	private ViewGroup root;
	private RelativeLayout layoutSubcategriesSelecting;
	private static final int SUBCAT_SELECTIONG_ANIMATION_DURATIO = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root = (ViewGroup) getLayoutInflater().inflate(R.layout.flipping_article_list_by_category, null);
		setContentView(root);
		categoryId = (Integer) ForBossUtils.getBundleData("category_id");
		
		// get subcat id
		Integer tempSubcategoryId = (Integer) ForBossUtils.getBundleData("sub_category_id");
		if (tempSubcategoryId != null) {
			subcategoryId = tempSubcategoryId.intValue();
		}

		// category text
		ImageView imgCategoryText = (ImageView) findViewById(R.id.imgCategoryText);
		if (subcategoryId == 0) {
			imgCategoryText.setImageBitmap(ForBossUtils.loadBitmapFromAssets(ForBossUtils.getConfig(Integer.toString(categoryId)), this));
		}
		else {
			imgCategoryText.setImageBitmap(ForBossUtils.loadBitmapFromAssets(
					CommonData.getInstance().getCategory(subcategoryId).getTextAssetPath(), this));
		}
		
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
		
		// layout to select subcategory
		initSubcategoriesSelecting();
		
		// flipper
		flipViewController = (FlipViewController) findViewById(R.id.flipViewController);
		flipViewControllerAdapter = new ArticleGroupAdapter(new ArrayList<ArticleGroup>());
		flipViewController.setAdapter(flipViewControllerAdapter);

		// load data
		final boolean needToDisplayProgressAlert = Article.count(this, categoryId, 0) < 3;
		if (needToDisplayProgressAlert) {
			ForBossUtils.alertProgress(this, "Đang tải dữ liệu...");
		} else {
			updateFlipperAdapter();
		}
		APIHelper.getInstance().getArticles(subcategoryId > 0 ? subcategoryId : categoryId, this, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (needToDisplayProgressAlert) ForBossUtils.dismissProgress(getContext());
				updateFlipperAdapter();
			}
		});
	}

	private void initSubcategoriesSelecting() {
		layoutSubcategriesSelecting = (RelativeLayout) findViewById(R.id.layoutSubcategriesSelecting);
		final LinearLayout layoutSubcategories = (LinearLayout) layoutSubcategriesSelecting.findViewById(R.id.layoutSubcategories);
		List<Category> listSubcategories = CommonData.getInstance().getSubcategories(categoryId);
		Locale locale = new Locale("vi");
		for (final Category subcat : listSubcategories) {
			// add view for each sub-category
			View viewSubcat = getLayoutInflater().inflate(R.layout.subcategory, null);
			viewSubcat.setLayoutParams(new LinearLayout.LayoutParams(
												LinearLayout.LayoutParams.MATCH_PARENT,
												ForBossUtils.convertDpToPixel(50, this)
												));
			
			ImageView imgIcon = (ImageView) viewSubcat.findViewById(R.id.imgIcon);
			imgIcon.setImageBitmap(ForBossUtils.loadBitmapFromAssets(subcat.getIconAssetPath(), this));
			
			TextView txtName = (TextView) viewSubcat.findViewById(R.id.txtName);
			txtName.setText(subcat.getTitle().toUpperCase(locale));
			if (subcat.getId() == subcategoryId) {
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
							ForBossUtils.putBundleData("category_id", categoryId);
							ForBossUtils.putBundleData("sub_category_id", subcat.getId());
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
		
		ImageButton buttonClose = (ImageButton) layoutSubcategriesSelecting.findViewById(R.id.buttonClose);
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
				lpOfLayoutSubcategories.topMargin = (layoutSubcategriesSelecting.getHeight() - layoutSubcategories.getHeight())/2;
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
	
	private void updateFlipperAdapter() {
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				List<ArticleGroup> list = ArticleGroup.loadArticleGroups(getContext(), categoryId, subcategoryId);
				flipViewControllerAdapter.getData().clear();
				flipViewControllerAdapter.getData().addAll(list);
				if (flipViewControllerAdapter.getData().size() != 0) {
					flipViewControllerAdapter.notifyDataSetChanged();
				}
				
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ForBossUtils.putBundleData("category_id", null);
		ForBossUtils.putBundleData("sub_category_id", null);
		
	}
	
	@Override
	public void onBackPressed() {
		if (layoutSubcategriesSelecting.getVisibility() == View.VISIBLE) {
			hideSubCategoriesSelecting(null);
		} else {
			super.onBackPressed();
		}
	}
}
