package com.forboss.bossmeasure;

import java.util.Stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.forboss.R;
import com.forboss.bossmeasure.SurveyData.Question;
import com.forboss.bossmeasure.SurveyData.Result;
import com.forboss.bossmeasure.SurveyData.SurveyItem;
import com.forboss.util.ForBossUtils;

public class BossMeasureActivity extends FragmentActivity {
	private FragmentManager fragmentManager;
	private Fragment currentFragment;
	private Stack<SurveyItem> surveyItemsStack = new Stack<SurveyData.SurveyItem>();
	private ImageButton buttonPrevSurveyItem;
	private ImageButton buttonNextSurveyItem;

	private static BossMeasureActivity instance;
	public static BossMeasureActivity getInstance() {
		return instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.boss_measure);
		SurveyData.init(this);

		buttonPrevSurveyItem = (ImageButton) findViewById(R.id.buttonPrevSurveyItem);
		buttonPrevSurveyItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				navToPrevSurveyItem();
			}
		});

		buttonNextSurveyItem = (ImageButton) findViewById(R.id.buttonNextSurveyItem);
		buttonNextSurveyItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				navToNextSurveyItem();
			}
		});
		
		ForBossUtils.UI.initHomeButton(this);

		fragmentManager = getSupportFragmentManager();

		navToFragment(new BossMeasureIntroFrament());
	}

	private void navToFragment(Fragment fragment) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		currentFragment = fragment;
		transaction.replace(R.id.layoutFragmentContainer, fragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();

		if (fragment instanceof BossMeasureQuestionFragment) {
			buttonPrevSurveyItem.setVisibility(surveyItemsStack.size() == 1 ? View.GONE : View.VISIBLE);
			buttonNextSurveyItem.setVisibility(View.VISIBLE);
		} else {
			buttonPrevSurveyItem.setVisibility(View.INVISIBLE);
			buttonNextSurveyItem.setVisibility(View.INVISIBLE);
		}
	}

	private void navToSurveyItem(SurveyItem item) {
		surveyItemsStack.push(item);
		if (item instanceof Question) {
			ForBossUtils.putBundleData("question", item);
			ForBossUtils.putBundleData("index", surveyItemsStack.indexOf(item));
			navToFragment(new BossMeasureQuestionFragment());
		} else if (item instanceof Result) {
			ForBossUtils.putBundleData("result", item);
			navToFragment(new BossMeasureResultFragment());
		}
	}

	public void startOver() {
		// clear all selections
		for (SurveyItem item : SurveyData.getAllSurveyItems()) {
			if (item instanceof Question) {
				((Question) item).clearAllSelections();
			}
		}

		// clear stack and current fragment
		surveyItemsStack.clear();
		currentFragment = null;

		// nav to first item
		navToSurveyItem(SurveyData.getFirstSurveyItem());
	}

	private void navToNextSurveyItem() {
		if (currentFragment != null && currentFragment instanceof BossMeasureQuestionFragment) {
			SurveyItem nextItem = ((BossMeasureQuestionFragment) currentFragment).getNextItem();
			if (nextItem == null) {
				ForBossUtils.alert(this, "Hãy chọn một đáp án");
				return;
			}
			navToSurveyItem(nextItem);
		}
	}

	private void navToPrevSurveyItem() {
		if (surveyItemsStack.size() >= 2) {
			surveyItemsStack.pop();
			navToSurveyItem(surveyItemsStack.pop());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
}
