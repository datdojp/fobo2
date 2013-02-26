package com.forboss.bossmeasure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.forboss.ForBossApplication;
import com.forboss.R;

public class BossMeasureIntroFrament extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.boss_measure_intro_frament, container, false);

		ImageButton buttonJoinSurvey = (ImageButton) root.findViewById(R.id.buttonJoinSurvey);
		buttonJoinSurvey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				BossMeasureActivity.getInstance().startOver();
			}
		});
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)buttonJoinSurvey.getLayoutParams();
		lp.leftMargin = (ForBossApplication.getWindowDisplay().getWidth() - lp.width) / 2;

		return root;
	}

}
