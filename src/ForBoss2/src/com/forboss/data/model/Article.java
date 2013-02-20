package com.forboss.data.model;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.forboss.data.util.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;

public class Article extends BaseModel {
	public Article loadFromJSON(JSONObject json) throws JSONException {
		id = json.getString("ID");
		title = unescape(json.getString("Title"));
		thumbnail = json.getString("Thumbnail");
		body = unescape(json.getString("Body"));
		views = json.getInt("Views");
		likes = json.getInt("Likes");
		link = json.getString("Link");
		createdTime = json.getLong("CreatedTime");
		return this;
	}
	
	public static List<Article> loadArticlesOrderedCreatedTimeDes(Context context, int categoryId, int subCategoryId) throws SQLException {
		Dao<Article, String> dao = DatabaseHelper.getHelper(context).getArticleDao();
		QueryBuilder<Article, String> builder = dao.queryBuilder();
		if (categoryId != 0) {
			builder.where().eq("categoryId", categoryId);
		}
		if (subCategoryId != 0) {
			builder.where().eq("subCategoryId", subCategoryId);
		}
		builder.orderBy("createdTime", false);
		return builder.query();
	}
	
	@DatabaseField(id=true)
	private String id;

	@DatabaseField
	private String title;

	@DatabaseField
	private String thumbnail;

	@DatabaseField
	private String body;
	
	@DatabaseField
	private String htmlContent;

	@DatabaseField
	private int categoryId;
	
	@DatabaseField
	private int subCategoryId;

	@DatabaseField
	private int views;

	@DatabaseField
	private int likes;

	@DatabaseField
	private String link;

	@DatabaseField
	private long createdTime;
	
	public Date getCreatedTimeInDate() {
		if (createdTime != 0) {
			return new Date(createdTime);
		}
		return null;
	}

	@DatabaseField
	private boolean isLiked;

	@DatabaseField
	private String pictureLocation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}


	public String getPictureLocation() {
		return pictureLocation;
	}

	public void setPictureLocation(String pictureLocation) {
		this.pictureLocation = pictureLocation;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public boolean isLiked() {
		return isLiked;
	}

	public void setLiked(boolean isLiked) {
		this.isLiked = isLiked;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(int subCategoryId) {
		this.subCategoryId = subCategoryId;
	}
}
