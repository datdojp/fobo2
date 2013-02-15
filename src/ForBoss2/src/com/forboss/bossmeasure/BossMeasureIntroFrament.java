package com.forboss.bossmeasure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.forboss.R;

public class BossMeasureIntroFrament extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.boss_measure_intro_frament, container);

		ImageButton buttonJoinSurvey = (ImageButton) root.findViewById(R.id.buttonJoinSurvey);
		buttonJoinSurvey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
			}
		});

		return root;
	}

}
