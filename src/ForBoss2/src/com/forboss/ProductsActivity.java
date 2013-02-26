package com.forboss;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ProductsActivity extends Activity {
	private static final int PRODUCT_IMAGE_WIDTH = 188;
	private static final int PRODUCT_IMAGE_HEIGHT = 280;
	private static final int PRODUCT_LINE_SCROLL_ANIMATION_DURATION = 300;
	private static final int PRODUCT_DETAIL_ANIMATION_DURATION = 300;
	private static final float PRODUCT_DETAIL_IMAGE_RATIO = 0.7f;
	private int productThumbnailWidth;
	private int productThumbnailHeight;
	private RelativeLayout layoutProductDetail;

	private static final int[][] PRODUCT_IMAGES_GOLD = new int[][] {
		new int[] {R.drawable.gold_product_1, R.drawable._1},
		new int[] {R.drawable.gold_product_2, R.drawable.p4},
		new int[] {R.drawable.gold_product_3, R.drawable.p6},
		new int[] {R.drawable.gold_product_4, R.drawable.p10},
		new int[] {R.drawable.gold_product_5, R.drawable.p11}
	};

	private static final int[][] PRODUCT_IMAGES_BLACK = new int[][] {
		new int[] {R.drawable.black_product_1, R.drawable._2},
		new int[] {R.drawable.black_product_2, R.drawable.p1},
		new int[] {R.drawable.black_product_3, R.drawable.p2},
		new int[] {R.drawable.black_product_4, R.drawable.p3},
		new int[] {R.drawable.black_product_5, R.drawable.p8},
		new int[] {R.drawable.black_product_6, R.drawable.p7},
		new int[] {R.drawable.black_product_7, R.drawable.p5},
		new int[] {R.drawable.black_product_8, R.drawable.p9}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.products);

		// button back
		ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// layout to show detail of product
		layoutProductDetail = (RelativeLayout) findViewById(R.id.layoutProductDetail);
		ImageButton buttonClose = (ImageButton) layoutProductDetail.findViewById(R.id.buttonClose);
		buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideProductDetail();
			}
		});

		// pre-calculated sizes of product thumbnails
		productThumbnailWidth = ForBossApplication.getWindowDisplay().getWidth() / 4;
		productThumbnailHeight = productThumbnailWidth * PRODUCT_IMAGE_HEIGHT / PRODUCT_IMAGE_WIDTH;

		// init first product line
		RelativeLayout layoutFirstProductLine = (RelativeLayout) findViewById(R.id.layoutFirstProductLine);
		initProductLine(PRODUCT_IMAGES_GOLD, layoutFirstProductLine);

		// init second product line
		RelativeLayout layoutSecondProductLine = (RelativeLayout) findViewById(R.id.layoutSecondProductLine);
		initProductLine(PRODUCT_IMAGES_BLACK, layoutSecondProductLine);
	}

	private void initProductLine(final int[][] arr2Res, final RelativeLayout layoutProductLine) {
		layoutProductLine.getLayoutParams().height = productThumbnailHeight;
		final LinearLayout layoutProductImages = (LinearLayout) layoutProductLine.findViewById(R.id.layoutProductImages) ;
		layoutProductImages.setTag(0);
		((RelativeLayout.LayoutParams)layoutProductImages.getLayoutParams()).rightMargin = - ForBossApplication.getWindowDisplay().getWidth();
		for(int i = 0; i < arr2Res.length; i++) {
			int[] arrRes = arr2Res[i];
			ImageView img = new ImageView(this);
			img.setLayoutParams(new LinearLayout.LayoutParams(productThumbnailWidth, productThumbnailHeight));
			img.setImageResource(arrRes[0]);
			img.setScaleType(ScaleType.FIT_XY);
			img.setTag(i);
			img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int idx = (Integer) v.getTag();
					if ((Integer)layoutProductImages.getTag() < 0) {
						idx += 4;
					}
					showProductDetail(arr2Res[idx][1]);
				}
			});
			layoutProductImages.addView(img);
		}

		ImageButton buttonLeft = (ImageButton) layoutProductLine.findViewById(R.id.buttonLeft);
		buttonLeft.bringToFront();
		buttonLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				scrollProductLine(layoutProductImages, 1);
			}
		});

		ImageButton buttonRight = (ImageButton) layoutProductLine.findViewById(R.id.buttonRight);
		buttonRight.bringToFront();
		buttonRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				scrollProductLine(layoutProductImages, -1);
			}
		});
	}

	private void scrollProductLine(LinearLayout layoutProductImages, int direction) {
		int curX = (Integer)layoutProductImages.getTag();
		int newX =  curX + ForBossApplication.getWindowDisplay().getWidth() * direction;
		newX = Math.max(-ForBossApplication.getWindowDisplay().getWidth(), newX);
		newX = Math.min(newX, 0);
		layoutProductImages.setTag(newX);		
		TranslateAnimation anim = new TranslateAnimation(curX, newX, 0, 0);
		anim.setDuration(PRODUCT_LINE_SCROLL_ANIMATION_DURATION);
		anim.setFillAfter(true);
		layoutProductImages.startAnimation(anim);
	}

	private void showProductDetail(int productResourceId) {
		Bitmap bm = BitmapFactory.decodeResource(getResources(), productResourceId);
		ImageView imgProductDetail = (ImageView) layoutProductDetail.findViewById(R.id.imgProductDetail);
		float screenRatio = ForBossApplication.getWindowDisplay().getHeight() / ForBossApplication.getWindowDisplay().getWidth(); 
		float bmRatio = bm.getHeight() / bm.getWidth();
		int width = 0;
		int height = 0;
		if (bmRatio > screenRatio) {
			height = Math.round(ForBossApplication.getWindowDisplay().getHeight() * PRODUCT_DETAIL_IMAGE_RATIO);
			width = height * bm.getWidth() / bm.getHeight();
		} else {
			width = Math.round(ForBossApplication.getWindowDisplay().getWidth() * PRODUCT_DETAIL_IMAGE_RATIO);
			height = width * bm.getHeight() / bm.getWidth();
		}
		RelativeLayout.LayoutParams lp = (LayoutParams) imgProductDetail.getLayoutParams();
		lp.width = width;
		lp.height = height;
		lp.leftMargin = (ForBossApplication.getWindowDisplay().getWidth() - width) / 2;
		lp.topMargin = (ForBossApplication.getWindowDisplay().getHeight() - height) / 2;
		imgProductDetail.setImageBitmap(bm);

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				layoutProductDetail.setVisibility(View.VISIBLE);
				Animation animation = new TranslateAnimation(0, 0, -layoutProductDetail.getHeight(), 0);
				animation.setDuration(PRODUCT_DETAIL_ANIMATION_DURATION);
				layoutProductDetail.startAnimation(animation);
			}
		});

	}

	private void hideProductDetail() {
		Animation animation = new TranslateAnimation(0, 0, 0, -layoutProductDetail.getHeight());
		animation.setDuration(PRODUCT_DETAIL_ANIMATION_DURATION);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				layoutProductDetail.setVisibility(View.INVISIBLE);
			}
		});
		layoutProductDetail.startAnimation(animation);
	}

}
