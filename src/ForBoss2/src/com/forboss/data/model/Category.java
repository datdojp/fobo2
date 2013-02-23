package com.forboss.data.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public class Category extends BaseModel {
	
	private static final Map<String, String> mappOfSubcatAndIcon = new HashMap<String, String>();
	static {
		mappOfSubcatAndIcon.put("Tin Tức", "icon_news.png");
		mappOfSubcatAndIcon.put("Giải Trí", "icon_entertainment.png");
		mappOfSubcatAndIcon.put("Góc Doanh Nhân", "icon_businesscorner.png");
		mappOfSubcatAndIcon.put("Phong Cách", "icon_style.png");
		mappOfSubcatAndIcon.put("Thể Thao", "icon_sport.png");
		mappOfSubcatAndIcon.put("Du Lịch", "icon_tourism.png");
		mappOfSubcatAndIcon.put("Thế Giới Số", "icon_digital.png");
		mappOfSubcatAndIcon.put("Sức Khỏe", "icon_health.png");
	}
	
	public String getIconAssetPath() {
		return mappOfSubcatAndIcon.get(title);
	}
	
	@Override
	protected List<String> getServerDataFieldNames() {
		if (serverDataFieldNames == null) {
			serverDataFieldNames = Arrays.asList("id", "title", "parentId");
		}
		return serverDataFieldNames;
	}
	
	public boolean isParent() {
		return parentId == 0;
	}
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
	
	@DatabaseField
	private long last;

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
	public long getLast() {
		return last;
	}
	public void setLast(long last) {
		this.last = last;
	}
}
