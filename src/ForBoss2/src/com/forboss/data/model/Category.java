package com.forboss.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public class Category extends BaseModel {
	public Category loadFromJSONObject(JSONObject json) throws JSONException {
		id = json.getInt("ID");
		title = unescape(json.getString("Title"));
		parentId = json.getInt("ParentID");
		return this;
	}
	
	@DatabaseField (id=true)
	private int id;
	
	@DatabaseField
	private int parentId;
	
	@DatabaseField
	private String title;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
