package com.forboss.bossmeasure;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.forboss.ForBossApplication;
import com.forboss.R;
import com.forboss.bossmeasure.SurveyData.Question.Option;

public class SurveyData {
	private static SurveyItem firstSurveyItem;
	private static List<SurveyItem> listSurveyItems;
	public static SurveyItem getFirstSurveyItem() {
		if (firstSurveyItem == null) {
			init();
		}
		return firstSurveyItem;
	}
	public static List<SurveyItem> getAllSurveyItems() {
		if (listSurveyItems == null) {
			init();
		}
		return listSurveyItems;
	}

	private static void init() {
		try {
			// read from survey.txt and parse to json object
			InputStream is = ForBossApplication.getAppContext().getResources().openRawResource(R.raw.survey);
			byte[] b = new byte[is.available()];
			is.read(b);
			JSONObject jsonEverything = new JSONObject(new String(b));
			listSurveyItems = new ArrayList<SurveyItem>();
			Map<String, SurveyItem> mapIdAndItems = new HashMap<String, SurveyData.SurveyItem>();

			// read questions
			JSONArray jsonQuestions = jsonEverything.getJSONArray("questions");
			for (int i = 0; i < jsonQuestions.length(); i++) {
				JSONObject jsonAQuestion = jsonQuestions.getJSONObject(i);
				Question quest = (Question) (new Question()).loadFromJSON(jsonAQuestion);
				listSurveyItems.add(quest);
				mapIdAndItems.put(quest.id, quest);

			}

			// read results
			JSONArray jsonResults = jsonEverything.getJSONArray("results");
			for (int i = 0; i < jsonResults.length(); i++) {
				JSONObject jsonAResult = jsonResults.getJSONObject(i);
				Result res = (Result) (new Result()).loadFromJSON(jsonAResult);
				listSurveyItems.add(res);
				mapIdAndItems.put(res.id, res);
			}				

			// wire everything together
			for (SurveyItem item : listSurveyItems) {
				if (!(item instanceof Question)) break;
				for (Option op : ((Question) item).options) {
					op.nextItem = mapIdAndItems.get(op.nextItemId);
				}
			}

			firstSurveyItem = listSurveyItems.get(0);
		} catch (Exception e) {
			Log.e(SurveyData.class.getName(), "Unable to read survey", e);
		}
	}

	public static abstract class SurveyItem {
		public String id;
		public abstract SurveyItem loadFromJSON(JSONObject data) throws JSONException;
	}

	public static class Question extends SurveyItem {
		public String content;
		public Option[] options;

		@Override
		public SurveyItem loadFromJSON(JSONObject data) throws JSONException {
			id = data.getString("id");
			content = data.getString("content");
			JSONArray jsonOptions = data.getJSONArray("options");
			options = new Option[jsonOptions.length()];
			for (int i = 0; i < jsonOptions.length(); i++) {
				JSONObject jsonAOption = jsonOptions.getJSONObject(i);
				Option op = new Option();
				op.content = jsonAOption.getString("content");
				op.nextItemId = jsonAOption.getString("next");
				options[i] = op;
			}
			return this;
		}
		
		public void clearAllSelections() {
			for(Option op : options) {
				op.isSelected = false;
			}
		}

		public static class Option {
			public String content;
			public SurveyItem nextItem;
			public String nextItemId;
			public boolean isSelected = false;
		}
	}

	public static class Result extends SurveyItem {
		public String summaryImagePath;
		public String detail;
		public String advice;

		@Override
		public SurveyItem loadFromJSON(JSONObject data) throws JSONException {
			id = data.getString("id");
			summaryImagePath = data.getString("summary_image_path");
			detail = data.getString("detail");
			advice = data.getString("advice");
			return this;
		}
	}
}
