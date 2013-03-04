package com.forboss.data.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

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
	public List<Category> getAllCategories(Context context) {
		if (allCategories == null) {
			load(context);
		}
		return allCategories;
	}
	
	private Map<Integer, Category> mapOfIdAndCategories;
	private Map<Integer, Category> getMapOfIdAndCategories(Context context) {
		if (mapOfIdAndCategories == null) {
			load(context);
		}
		return mapOfIdAndCategories;
	}
	
	public Category getCategory(int categoryId, Context context) {
		return getMapOfIdAndCategories(context).get(categoryId);
	}
	
	public List<Category> getSubcategories(int categoryId, Context context) {
		List<Category> results = new ArrayList<Category>();
		for (Category category : getAllCategories(context)) {
			if (category.getParentId() == categoryId) {
				results.add(category);
			}
		}
		return results;
	}
	
}
