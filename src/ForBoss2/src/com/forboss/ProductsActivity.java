package com.forboss;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.forboss.util.ForBossUtils;

public class ProductsActivity extends BaseActivity {
	private static final int PRODUCT_IMAGE_WIDTH = 2162;
	private static final int PRODUCT_IMAGE_HEIGHT = 2862;
	private static final int PRODUCT_LINE_SCROLL_ANIMATION_DURATION = 300;
	private static final int PRODUCT_DETAIL_ANIMATION_DURATION = 300;
	private static final float PRODUCT_DETAIL_IMAGE_RATIO = 0.7f;
	private int productThumbnailWidth;
	private int productThumbnailHeight;
	private RelativeLayout layoutProductDetail;
	private ImageButton buttonClose;

	private static final int[][] PRODUCT_IMAGES_GOLD = new int[][] {
		new int[] {R.drawable.gold_product_1, R.drawable._1},
		new int[] {R.drawable.gold_product_2, R.drawable.p4},
		new int[] {R.drawable.gold_product_3, R.drawable.p6},
		new int[] {R.drawable.gold_product_4, R.drawable.p10},
		new int[] {R.drawable.gold_product_5, R.drawable.p11},
		new int[] {R.drawable.gold_product_6, R.drawable.p12}
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
		buttonClose = (ImageButton) layoutProductDetail.findViewById(R.id.buttonClose);
		buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideProductDetail();
			}
		});

		// pre-calculated sizes of product thumbnails
		productThumbnailWidth = ForBossUtils.App.getWindowDisplay().getWidth() / 4;
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
		((RelativeLayout.LayoutParams)layoutProductImages.getLayoutParams()).rightMargin = - ForBossUtils.App.getWindowDisplay().getWidth();
		for(int i = 0; i < arr2Res.length; i++) {
			int[] arrRes = arr2Res[i];
			ImageView img = new ImageView(this);
			img.setLayoutParams(new LinearLayout.LayoutParams(productThumbnailWidth, productThumbnailHeight));		
			img.setScaleType(ScaleType.FIT_XY);
			img.setTag(i);
//			img.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					int idx = (Integer) v.getTag();
//					if ((Integer)layoutProductImages.getTag() < 0) {
//						idx += 4;
//					}
//					showProductDetail(arr2Res[idx][1]);
//				}
//			});
			
			// set padding left for special images
			if (arrRes[0] == R.drawable.gold_product_1 || arrRes[0] == R.drawable.black_product_1 
					|| arrRes[0] == R.drawable.black_product_7) {
				Bitmap bm = BitmapFactory.decodeResource(getResources(), arrRes[0]);
				img.setImageBitmap(bm);
				int left = productThumbnailWidth - bm.getWidth() * productThumbnailHeight / bm.getHeight();
				img.setPadding(left, 0, 0, 0);
			} else {
				img.setImageResource(arrRes[0]);
			}
			
			layoutProductImages.addView(img);
		}

		final ImageView imgLeft = (ImageView) layoutProductLine.findViewById(R.id.imgLeft);
		imgLeft.bringToFront();
		imgLeft.setVisibility(View.INVISIBLE);
//		imgLeft.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				scrollProductLine(layoutProductImages, 1);
//			}
//		});

		final ImageView imgRight = (ImageView) layoutProductLine.findViewById(R.id.imgRight);
		imgRight.bringToFront();
//		imgRight.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				scrollProductLine(layoutProductImages, -1);
//			}
//		});
		
		final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				int idx = Math.round(e.getX()) / productThumbnailWidth;
				if ((Integer)layoutProductImages.getTag() < 0) {
					idx += 4;
				}
				if (idx < arr2Res.length) {
					showProductDetail(arr2Res[idx][1]);
				}
				return true;
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (velocityX > 0) {
					scrollProductLine(layoutProductImages, 1, imgLeft, imgRight);
				} else {
					scrollProductLine(layoutProductImages, -1, imgLeft, imgRight);
				}
				return true;
			}
		});
		layoutProductLine.requestDisallowInterceptTouchEvent(true);
		layoutProductLine.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}

	private void scrollProductLine(LinearLayout layoutProductImages, int direction, ImageView imgLeft, ImageView imgRight) {
		int curX = (Integer)layoutProductImages.getTag();
		int newX =  curX + ForBossUtils.App.getWindowDisplay().getWidth() * direction;
		newX = Math.max(-ForBossUtils.App.getWindowDisplay().getWidth(), newX);
		newX = Math.min(newX, 0);
		layoutProductImages.setTag(newX);		
		TranslateAnimation anim = new TranslateAnimation(curX, newX, 0, 0);
		anim.setDuration(PRODUCT_LINE_SCROLL_ANIMATION_DURATION);
		anim.setFillAfter(true);
		layoutProductImages.startAnimation(anim);
		
		if (direction == 1) {
			imgLeft.setVisibility(View.INVISIBLE);
			imgRight.setVisibility(View.VISIBLE);
		} else {
			imgLeft.setVisibility(View.VISIBLE);
			imgRight.setVisibility(View.INVISIBLE);
		}
	}

	private void showProductDetail(int productResourceId) {
		Bitmap bm = BitmapFactory.decodeResource(getResources(), productResourceId);
		ImageView imgProductDetail = (ImageView) layoutProductDetail.findViewById(R.id.imgProductDetail);
		float screenRatio = 1f * ForBossUtils.App.getWindowDisplay().getHeight() / ForBossUtils.App.getWindowDisplay().getWidth(); 
		float bmRatio = 1.0f * bm.getHeight() / bm.getWidth();
		int width = 0;
		int height = 0;
		if (bmRatio > screenRatio) {
			height = Math.round(ForBossUtils.App.getWindowDisplay().getHeight() * PRODUCT_DETAIL_IMAGE_RATIO);
			width = height * bm.getWidth() / bm.getHeight();
		} else {
			width = Math.round(ForBossUtils.App.getWindowDisplay().getWidth() * PRODUCT_DETAIL_IMAGE_RATIO);
			height = width * bm.getHeight() / bm.getWidth();
		}
		RelativeLayout.LayoutParams lp = (LayoutParams) imgProductDetail.getLayoutParams();
		lp.width = width;
		lp.height = height;
		lp.leftMargin = (ForBossUtils.App.getWindowDisplay().getWidth() - width) / 2;
		lp.topMargin = Math.min(
						(ForBossUtils.App.getWindowDisplay().getHeight() - height) / 2,
						ForBossUtils.App.getWindowDisplay().getHeight() - buttonClose.getLayoutParams().height/2 - height - ForBossUtils.convertDpToPixel(5, this)); // need to do this so that button close is always displayed
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

	
	@Override
	public void onBackPressed() {
		if (layoutProductDetail.getVisibility() == View.VISIBLE) {
			hideProductDetail();
		} else {
			super.onBackPressed();
		}
	}
}
