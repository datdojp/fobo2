package com.forboss.data.model;

import java.sql.SQLException;
import java.util.List;

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
			// load all categories
			allCategories = DatabaseHelper.getHelper(context).getCategoryDao().queryForAll();
		} catch (SQLException e) {
			Log.e(this.getClass().getName(), "Database error", e);
		}
	}
	
	private List<Category> allCategories;
	public List<Category> getAllCategories() {
		return allCategories;
	}
	
}
