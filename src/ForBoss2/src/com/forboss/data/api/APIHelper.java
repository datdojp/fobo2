package com.forboss.data.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.forboss.data.model.Article;
import com.forboss.data.model.Category;
import com.forboss.data.util.DatabaseHelper;
import com.forboss.util.ForBossUtils;
import com.j256.ormlite.dao.Dao;

public class APIHelper {
	private static APIHelper instance;
	public static APIHelper getInstance() {
		if (instance == null) {
			instance = new APIHelper();
		}
		return instance;
	}

	public void getCategories(final Context context, final Handler finishHandler) {
		APIClient.getClient().getCategories(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					// clear all categories
					Dao<Category, Integer> dao = DatabaseHelper.getHelper(context).getCategoryDao();
					dao.deleteBuilder().delete();

					// insert newer categories from server
					String jsonString = (String) msg.obj;
					JSONArray jsonArray = new JSONArray(jsonString);
					for (int i = 0; i < jsonArray.length(); i++) {
						Category category = (new Category()).loadFromJSONObject(jsonArray.getJSONObject(i));
						dao.create(category);
					}
					if (finishHandler != null) {
						finishHandler.sendEmptyMessage(0);
					}
				} catch (JSONException e) {
					Log.e(this.getClass().getName(), "Unable to parse json string", e);
				} catch (SQLException e) {
					Log.e(this.getClass().getName(), "Database error", e);
				}

			}
		}, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (finishHandler != null) {
					finishHandler.sendEmptyMessage(0);
				}
			}
		});
	}
	
	public void getArticles(final String categoryId, final Context context, final Handler finishHandler) {
		int start = 1;
		final int n = 3;
		getArticles(categoryId, start, n, context, finishHandler);
	}
	
	private void getArticles(final String categoryId, final int start, final int n, final Context context, final Handler finishHandler) {
		APIClient.getClient().getArticleForCategory(categoryId, start, start + n, 
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String jsonString = (String)msg.obj;
				try {
					JSONArray jsonArticles = new JSONArray(jsonString);
					List<Article> listArticles = new ArrayList<Article>();
					for (int i = 0; i < jsonArticles.length(); i++) {
						listArticles.add((new Article()).loadFromJSON(jsonArticles.getJSONObject(i)));
					}
					Dao<Article, String> dao = DatabaseHelper.getHelper(context).getArticleDao();
					boolean hasLoadedArticle = false;
					for (Article article : listArticles) {
						Article dbArticle = dao.queryForId(article.getId());
						if (dbArticle != null) {
							hasLoadedArticle = true;
							// TODO: should update article that is from server to article that is in database
						} else {
							dao.create(article);
						}
					}
					SharedPreferences pref = ForBossUtils.Storage.getSharedPreferences(context);
					boolean hasPreviousErrorAtArticleLoading = pref.getBoolean(
																ForBossUtils.Storage.SHARED_PREFERENCES_KEY_HAS_PREVIOUS_ERROR_AT_ARTICLE_LOADING, false);
					if (listArticles.size() < n || (hasLoadedArticle && !hasPreviousErrorAtArticleLoading)) {
						pref.edit()
							.putBoolean(ForBossUtils.Storage.SHARED_PREFERENCES_KEY_HAS_PREVIOUS_ERROR_AT_ARTICLE_LOADING, false)
							.commit();
						if (finishHandler != null) finishHandler.sendEmptyMessage(0);
					} else {
						getArticles(categoryId, start + n, start + n + n - 1, context, finishHandler);
					}
				} catch (JSONException e) {
					Log.e(this.getClass().getName(), "Unable to parse json data from server:" + jsonString, e);
				} catch (SQLException e) {
					Log.e(this.getClass().getName(), null, e);
				} 
			}
		}, 
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ForBossUtils.Storage.getSharedPreferences(context).edit()
					.putBoolean(ForBossUtils.Storage.SHARED_PREFERENCES_KEY_HAS_PREVIOUS_ERROR_AT_ARTICLE_LOADING, true)
					.commit();
				if (finishHandler != null) finishHandler.sendEmptyMessage(0);
			}
		});
	}
}
