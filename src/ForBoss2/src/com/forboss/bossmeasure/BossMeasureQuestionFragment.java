package com.forboss.bossmeasure;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forboss.R;
import com.forboss.bossmeasure.SurveyData.Question;
import com.forboss.bossmeasure.SurveyData.SurveyItem;
import com.forboss.bossmeasure.SurveyData.Question.Option;
import com.forboss.util.ForBossUtils;

public class BossMeasureQuestionFragment extends Fragment {
	private Question question;
	private List<CheckBox> listAllCheckboxes = new ArrayList<CheckBox>();	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.boss_measure_question_fragment, container, false);

		// get question from bundle
		question = (Question) ForBossUtils.getBundleData("question");
		Integer index = (Integer) ForBossUtils.getBundleData("index");		

		// set question content
		TextView textQuestionContent = (TextView) root.findViewById(R.id.textQuestionContent);
		textQuestionContent.setText((index+1) + ". " + question.content);

		// create option
		LinearLayout layoutOptions = (LinearLayout) root.findViewById(R.id.layoutOptions);
		for (Option op : question.options) {
			CheckBox checkBox = new CheckBox(getActivity());
			checkBox.setText(op.content);
			checkBox.setTextAppearance(getActivity(), R.style.survey_white_text);
			checkBox.setChecked(op.isSelected);
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton view, boolean isChecked) {
					((Option)view.getTag()).isSelected = isChecked;
					if (isChecked) {
						for (CheckBox checkBox : listAllCheckboxes) {
							if (checkBox == view) continue;
							checkBox.setChecked(false);
							((Option)checkBox.getTag()).isSelected = false;
						}
					}
				}
			});
			checkBox.setTag(op);
			checkBox.setLayoutParams(
					new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));			
			((LinearLayout.LayoutParams)checkBox.getLayoutParams()).setMargins(0, op == question.options[0] ? 0 : 27, 0, 0);
			layoutOptions.addView(checkBox);
			listAllCheckboxes.add(checkBox);
		}

		return root;
	}

	public SurveyItem getNextItem() {
		for (Option op : question.options) {
			if (op.isSelected) return op.nextItem;
		}
		return null;
	}
}
