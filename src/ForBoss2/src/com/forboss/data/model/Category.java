package com.forboss.data.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public class Category extends BaseModel {
	
	private static final Map<String, String[]> mappOfSubcatAndIcon = new HashMap<String, String[]>();
	static {
		mappOfSubcatAndIcon.put("Tin Tức", new String[] {"icon_news.png", "Tintuc.png"});
		mappOfSubcatAndIcon.put("Giải Trí", new String[] {"icon_entertainment.png", "Giaitri.png"});
		mappOfSubcatAndIcon.put("Góc Doanh Nhân", new String[] {"icon_businesscorner.png", "Gocdoanhnhan.png"});
		mappOfSubcatAndIcon.put("Phong Cách", new String[] {"icon_style.png", "Phongcach.png"});
		mappOfSubcatAndIcon.put("Thể Thao", new String[] {"icon_sport.png", "Thethao.png"});
		mappOfSubcatAndIcon.put("Du Lịch", new String[] {"icon_tourism.png", "Dulich.png"});
		mappOfSubcatAndIcon.put("Thế Giới Số", new String[] {"icon_digital.png", "Thegioiso.png"});
		mappOfSubcatAndIcon.put("Sức Khỏe", new String[] {"icon_health.png", "Suckhoe.png"});
		mappOfSubcatAndIcon.put("Sự Kiện", new String[] {null, "Sukien.png"});
		
	}
	
	public String getIconAssetPath() {
		return mappOfSubcatAndIcon.get(title)[0];
	}
	public String getTextAssetPath() {
		return mappOfSubcatAndIcon.get(title)[1];
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
