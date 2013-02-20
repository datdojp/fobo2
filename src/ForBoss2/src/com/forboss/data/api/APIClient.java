package com.forboss.data.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.forboss.BuildConfig;
import com.forboss.util.ForBossUtils;

public class APIClient {
	private HttpClient client;

	private static APIClient instance;

	public static APIClient getClient() {
		if (instance == null) {
			instance = new APIClient();
		}
		return instance;
	}

	public static final int SC_CONNECTION_ERROR = -1;

	private APIClient() {
		client = new DefaultHttpClient();
	}

	public void signup(String email, String phoneNumber, String cmnd, Handler successHandler, Handler failureHandler) {
		template(	new String[] {"email", email, "phone", phoneNumber, "id", cmnd}, 
					"login.aspx", HttpMethod.GET, successHandler, failureHandler	);
	}
	
	public void getCategories(Handler successHandler, Handler failureHandler) {
		template (new String[] {}, "categories.aspx", HttpMethod.GET, successHandler, failureHandler);
	}
	
	public void getArticleForCategory(String categoryId, int start, int end, Handler successHandler, Handler failureHandler) {
		template(	new String[] {"category", categoryId, "start", Integer.toString(start), "end", Integer.toString(end)}, 
					"posts.aspx", HttpMethod.GET, successHandler, failureHandler	);
	}
	
	public void getArticleDetail(String articleId, Handler successHandler, Handler failureHandler) {
		template(	new String[] {"id", articleId}, 
					"post.aspx", HttpMethod.GET, successHandler, failureHandler	);
	}
	
	public void likeArticle(String articleId, Handler successHandler, Handler failureHandler) {
		template(new String[] {"id", articleId}, "like.aspx", HttpMethod.GET, successHandler, failureHandler);
	}
	
	private void template(String[] paramsKeyVal, String path, HttpMethod method, Handler successHandler, Handler failureHandler) {
		assert(paramsKeyVal.length % 2 == 0);
		JSONObject jsonParams = new JSONObject();
		try {
			for (int i = 0; i < paramsKeyVal.length/2; i++) {
				jsonParams.put(paramsKeyVal[i*2], paramsKeyVal[i*2+1]);
			}
		} catch (JSONException e) {
			// ignore
		}
		(new RequestTask()).setHandlers(successHandler, failureHandler)
			.execute(new String[] {path, method.name(), jsonParams.toString()});
	}

	private class RequestTask extends AsyncTask<String, Integer, Integer> {
		String jsonString;

		private Handler successHandler;

		private Handler failureHandler;

		public RequestTask setHandlers(Handler successHandler, Handler failureHandler) {
			this.successHandler = successHandler;
			this.failureHandler = failureHandler;
			return this;
		}

		@Override
		protected Integer doInBackground(String... params) {
			String path = params[0];
			String httpMethod = params[1];
			String jsonParams = params[2];
			HttpRequestBase request = null;
			if (httpMethod.equals(HttpMethod.GET.name())) {
				// parse the json params into BasicNameValuePair
				try {
					String extraURL = "";
					if (jsonParams != null) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						JSONObject jsonObject = new JSONObject(jsonParams);
						Iterator<Object> iterator = (Iterator<Object>)jsonObject.keys();
						while (iterator.hasNext()) {
							String key = iterator.next().toString();
							Object value = jsonObject.get(key);
							if (!(value instanceof String)) {
								// GET request only allow string parameters
								continue;
							}
							nameValuePairs.add(new BasicNameValuePair(key, (String)value));
						}
						extraURL = URLEncodedUtils.format(nameValuePairs, "utf-8");
					} 
					request = new HttpGet(ForBossUtils.getBaseApi() + path + "?" + extraURL);
				} catch (JSONException e) {
					Log.e(this.getClass().getName(), "parse params into json failed!");
				}
			} else if (httpMethod.equals(HttpMethod.POST.name())) {
				request = new HttpPost(ForBossUtils.getBaseApi() + path);
				if (jsonParams != null) {
					try {
						StringEntity entity = new StringEntity(jsonParams);
						entity.setContentType("application/json");
						((HttpEntityEnclosingRequestBase)request).setEntity(entity);
					} catch (UnsupportedEncodingException e) {
						Log.e(this.getClass().getName(), "string encode from json params failed!");
					}
				}
			} else {
				Log.e(this.getClass().getName(), "unsupported http method");
				return null;
			}

			jsonString = null;
			try {
				HttpResponse response = client.execute(request);
				jsonString = EntityUtils.toString(response.getEntity());
				if (BuildConfig.DEBUG) {
					Log.d(this.getClass().getName(), "REQUEST:" + request.getURI());
					Log.d(this.getClass().getName(), "RESPONSE:" + jsonString);
				}
				return response.getStatusLine().getStatusCode();
			} catch (ClientProtocolException e) {
				Log.e(this.getClass().getName(), "http protocol error");
				return SC_CONNECTION_ERROR;
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "connection abort");
				return SC_CONNECTION_ERROR;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			Handler handler = null;
			if (result == HttpStatus.SC_OK) {
				handler = successHandler;
			} else {
				handler = failureHandler;
			}
			if (handler != null) {
				Message message = handler.obtainMessage();
				message.what = result;
				message.obj = jsonString;
				handler.sendMessage(message);
			}
		}
	}

	private enum HttpMethod {
		GET,
		POST
	}
}
