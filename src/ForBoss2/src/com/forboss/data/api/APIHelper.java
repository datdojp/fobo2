package com.forboss.data.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.forboss.data.model.Article;
import com.forboss.data.model.ArticleGroup;
import com.forboss.data.model.BaseModel;
import com.forboss.data.model.Category;
import com.forboss.data.model.CommonData;
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

					// insert newer categories from server
					String jsonString = (String) msg.obj;
					JSONArray jsonArray = new JSONArray(jsonString);
					for (int i = 0; i < jsonArray.length(); i++) {
						Category category = (new Category()).loadFromJSONObject(jsonArray.getJSONObject(i));
						Category dbCategory = dao.queryForId(category.getId());
						if (dbCategory != null) {
							if (!dbCategory.dataIdenticalTo(category)) {
								dbCategory.copyFrom(category);
								dao.update(dbCategory);
							}
						} else {
							dao.create(category);
						}
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
				if (finishHandler != null) finishHandler.sendEmptyMessage(0);
			}
		});
	}

	public void getArticles(final int categoryId, final Context context, final Handler finishHandler) {
		getArticles(categoryId, 1, Integer.MAX_VALUE, context, finishHandler);
	}

	private void getArticles(final int categoryId, final int start, final int end, final Context context, final Handler finishHandler) {
		final Category category = CommonData.getInstance().getCategory(categoryId);
		APIClient.getClient().getArticleForCategory(categoryId, start, end, category.getLast(), 
				new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String jsonString = (String)msg.obj;
				try {
					Dao<Article, String> articleDao = DatabaseHelper.getHelper(context).getArticleDao();
					Dao<Category, Integer> categoryDao = DatabaseHelper.getHelper(context).getCategoryDao();
					JSONObject jsonResult = new JSONObject(jsonString);
					JSONArray jsonArticles = jsonResult.getJSONArray("results");
					long last = jsonResult.getLong("last");
					boolean needRefresh = false;
					for (int i = 0; i < jsonArticles.length(); i++) {
						Article article = new Article().loadFromJSON(jsonArticles.getJSONObject(i));
						Article dbArticle = articleDao.queryForId(article.getId());
						if (dbArticle != null) {
							if (!dbArticle.dataIdenticalTo(article)) {
								dbArticle.copyFrom(article);
								articleDao.update(dbArticle);
								needRefresh = true;
							}
						} else {
							articleDao.create(article);
							needRefresh = true;
						}
					}
					category.setLast(last);
					categoryDao.update(category);
					if (finishHandler != null) {
						Message msg1 = finishHandler.obtainMessage();
						msg1.obj = needRefresh;
						finishHandler.sendMessage(msg1);
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
				if (finishHandler != null) finishHandler.sendEmptyMessage(0);
			}
		});
	}

	public void getImages(List list, Context context, Handler finishOneImageHandler, Handler finishAllHandler) {
		new ImageLoadingAsyncTask()
		.setContext(context)
		.setFinishOneImageHandler(finishOneImageHandler)
		.setFinishAllHandler(finishAllHandler)
		.execute(list);
	}

	private class ImageLoadingAsyncTask extends AsyncTask<List, Integer, Integer> {
		private Context context;
		private Handler finishOneImageHandler;
		private Handler finishAllHandler;

		public ImageLoadingAsyncTask setContext(Context context) {
			this.context = context;
			return this;
		}
		public ImageLoadingAsyncTask setFinishOneImageHandler(Handler finishOneImageHandler) {
			this.finishOneImageHandler = finishOneImageHandler;
			return this;
		}
		public ImageLoadingAsyncTask setFinishAllHandler(Handler finishAllHandler) {
			this.finishAllHandler = finishAllHandler;
			return this;
		}

		@Override
		protected Integer doInBackground(List... args) {
			List list = args[0];
			try {
				Dao<Article, String> dao = DatabaseHelper.getHelper(context).getArticleDao();
				for (Object obj : list) {
					Article[] arrArticles;
					if (obj instanceof Article) {
						arrArticles = new Article[] {(Article) obj};
					} else {
						arrArticles = ((ArticleGroup)obj).getAll();
					}
					for (Article article : arrArticles) {
						if (article != null && article.getThumbnail() != null && article.getPictureLocation() == null) {
							String filename = "forboss2_article_thumbnail_" + article.getId();
							ForBossUtils.downloadAndSaveToInternalStorage(article.getThumbnail(), filename, (ContextWrapper)context);
							article.setPictureLocation(filename);
							dao.update(article);
							if (finishOneImageHandler != null) {
								Message msg = finishOneImageHandler.obtainMessage();
								msg.obj = article;
								finishOneImageHandler.sendMessage(msg);
							}
						}
					}
				}
				if (finishAllHandler != null) finishAllHandler.sendEmptyMessage(0);
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "Unable to save image to internal storage", e);
			} catch (SQLException e) {
				Log.e(this.getClass().getName(), "Database error", e);
			}

			return null;
		}

	}

	public void getArticleDetail(final Article article, final Context context, final Handler finishHandler) {
		APIClient.getClient().getArticleDetail(article.getId(), new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					Dao<Article, String> dao = DatabaseHelper.getHelper(context).getArticleDao();
					JSONObject jsonObject = new JSONObject((String)msg.obj);
					String html = BaseModel.unescape(jsonObject.getString("Body"));
					boolean needRefresh = false;
					if (!html.equals(article.getHtmlContent())) {
						article.setHtmlContent(html);
						dao.update(article);
						needRefresh = true;
					}
					if (finishHandler != null) {
						Message msg1 = finishHandler.obtainMessage();
						msg1.obj = new Object[] {article, needRefresh};
						finishHandler.sendMessage(msg1);
					}
				} catch (JSONException e) {
					Log.e(this.getClass().getName(), "Json error", e);
				} catch (SQLException e) {
					Log.e(this.getClass().getName(), "Database error", e);
				}

			}
		}, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (finishHandler != null) finishHandler.sendEmptyMessage(0);
			}
		});
	}
}
