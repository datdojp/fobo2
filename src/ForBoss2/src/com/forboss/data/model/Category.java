package com.forboss.data.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;

import com.forboss.ForBossApplication;
import com.forboss.util.ForBossUtils;
import com.j256.ormlite.field.DatabaseField;

public class Category extends BaseModel {

	public static final int CATEGORY_ATTENTION_ID=1;
	public static final int CATEGORY_LEVEL_ID=2;
	public static final int CATEGORY_STYLE_ID=3;
	public static final int CATEGORY_SUCCESS_ID=4;
	public static final int CATEGORY_EVENT_ID=25;

	private static final Map<String, String[]> mapOfSubcatAndIcon = new HashMap<String, String[]>();
	static {
		mapOfSubcatAndIcon.put("tin tức", new String[] {"icon_news.png", "Tintuc.png"});
		mapOfSubcatAndIcon.put("giải trí", new String[] {"icon_entertainment.png", "Giaitri.png"});
		mapOfSubcatAndIcon.put("góc doanh nhân", new String[] {"icon_businesscorner.png", "Gocdoanhnhan.png"});
		mapOfSubcatAndIcon.put("phong cách", new String[] {"icon_style.png", "Phongcach.png"});
		mapOfSubcatAndIcon.put("thể thao", new String[] {"icon_sport.png", "Thethao.png"});
		mapOfSubcatAndIcon.put("du lịch", new String[] {"icon_tourism.png", "Dulich.png"});
		mapOfSubcatAndIcon.put("thế giới số", new String[] {"icon_digital.png", "Thegioiso.png"});
		mapOfSubcatAndIcon.put("sức khỏe", new String[] {"icon_health.png", "Suckhoe.png"});
		mapOfSubcatAndIcon.put("lôi cuốn", new String[] {null, "Loi cuon.png"});
		mapOfSubcatAndIcon.put("đẳng cấp", new String[] {null, "Dang cap.png"});
		mapOfSubcatAndIcon.put("lịch lãm", new String[] {null, "Lich lam.png"});
		mapOfSubcatAndIcon.put("thành đạt", new String[] {null, "Thanh dat.png"});
		mapOfSubcatAndIcon.put("sự kiện", new String[] {null, "Sukien.png"});
	}

	public Bitmap getIconBitmap(Context context) {
		String path = mapOfSubcatAndIcon.get(title.toLowerCase(ForBossApplication.getDefaultLocale()))[0];
		return ForBossUtils.loadBitmapFromAssets(path, context);
	}
	public Bitmap getTextBitmap(Context context) {
		String path = mapOfSubcatAndIcon.get(title.toLowerCase(ForBossApplication.getDefaultLocale()))[1];
		return ForBossUtils.loadBitmapFromAssets(path, context);
	}
	
	public int getQueryCategoryId() {
		if (isParent())
			return id;
		else
			return parentId;
	}
	public int getQuerySubcategoryId() {
		if (isParent())
			return 0;
		else
			return id;
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
