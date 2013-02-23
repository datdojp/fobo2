package com.forboss.data.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.forboss.ForBossApplication;
import com.forboss.data.util.DatabaseHelper;

public class CommonData {
	private static CommonData instance;
	public static CommonData getInstance() {
		if (instance == null) {
			instance = new CommonData();
		}
		return instance;
	}
	
	public void load(Context context){
		try {
			allCategories = DatabaseHelper.getHelper(context).getCategoryDao().queryForAll();
			mapOfIdAndCategories = new HashMap<Integer, Category>();
			for (Category cat : allCategories) {
				mapOfIdAndCategories.put(cat.getId(), cat);
			}
		} catch (SQLException e) {
			Log.e(this.getClass().getName(), "Database error", e);
		}
	}
	
	private List<Category> allCategories;
	public List<Category> getAllCategories() {
		if (allCategories == null) {
			load(ForBossApplication.getAppContext());
		}
		return allCategories;
	}
	
	private Map<Integer, Category> mapOfIdAndCategories;
	private Map<Integer, Category> getMapOfIdAndCategories() {
		if (mapOfIdAndCategories == null) {
			load(ForBossApplication.getAppContext());
		}
		return mapOfIdAndCategories;
	}
	
	public Category getCategory(int categoryId) {
		return getMapOfIdAndCategories().get(categoryId);
	}
	
	public List<Category> getSubcategories(int categoryId) {
		List<Category> results = new ArrayList<Category>();
		for (Category category : getAllCategories()) {
			if (category.getParentId() == categoryId) {
				results.add(category);
			}
		}
		return results;
	}
	
}
