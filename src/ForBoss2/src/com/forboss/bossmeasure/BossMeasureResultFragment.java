package com.forboss.bossmeasure;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.forboss.R;
import com.forboss.bossmeasure.SurveyData.Result;
import com.forboss.util.ForBossUtils;

public class BossMeasureResultFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.boss_measure_result_fragment, container, false);
		Result result = (Result) ForBossUtils.getBundleData("result");

		// set summary
		ImageView imgSummary = (ImageView) root.findViewById(R.id.imgSummary);
		Bitmap bmSummary = ForBossUtils.loadBitmapFromAssets(result.summaryImagePath, getActivity());
		imgSummary.setImageBitmap(bmSummary);
		
		// set detail
		TextView textDetail = (TextView) root.findViewById(R.id.textDetail);
		textDetail.setText(result.detail);
		
		// set advice
		TextView textAdvice = (TextView) root.findViewById(R.id.textAdvice);
		textAdvice.setText(result.advice);
		
		// continue survey button
		ImageButton buttonContinueSurvey = (ImageButton) root.findViewById(R.id.buttonContinueSurvey);
		buttonContinueSurvey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BossMeasureActivity.getInstance().startOver();
			}
		});
		
		return root;
	}

}