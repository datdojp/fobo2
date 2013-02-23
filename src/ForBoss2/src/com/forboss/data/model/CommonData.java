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
			mappingOfIdAndCategories = new HashMap<Integer, Category>();
			for (Category cat : allCategories) {
				mappingOfIdAndCategories.put(cat.getId(), cat);
			}
		} catch (SQLException e) {
			Log.e(this.getClass().getName(), "Database error", e);
		}
	}
	
	private List<Category> allCategories;
	public List<Category> getAllCategories() {
		return allCategories;
	}
	
	private Map<Integer, Category> mappingOfIdAndCategories;
	public Category getCategory(int categoryId) {
		return mappingOfIdAndCategories.get(categoryId);
	}
	
	public List<Category> getSubcategories(int categoryId) {
		List<Category> results = new ArrayList<Category>();
		for (Category category : allCategories) {
			if (category.getParentId() == categoryId) {
				results.add(category);
			}
		}
		return results;
	}
	
}
