package com.forboss.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.forboss.FeatureSelectingActivity;
import com.forboss.ForBossApplication;
import com.forboss.MainActivity;
import com.forboss.R;

public class ForBossUtils {
	private static Map<String, Object> bundleData = new HashMap<String, Object>();
	public static Object getBundleData(final String name) {
		return bundleData.get(name);
	}
	public static void putBundleData(final String name, final Object data) {
		bundleData.put(name, data);
	}

	/**
	 * This method downloads a file from given <code>url</code> and save it as <code>fileName</code> to internal storage
	 * @param url
	 * @param fileName
	 * @param contextWrapper the activity where this method is called
	 * @throws IOException
	 */
	public static void downloadAndSaveToInternalStorage(String url, final String fileName, final ContextWrapper contextWrapper) throws IOException {
		// check file existing
		final File file = contextWrapper.getFileStreamPath(fileName);
		if (file.exists()) {
			Log.d(ForBossUtils.class.getName(), "File \"" + fileName + "\" exists in system. No need to download.");
			return;
		}

		url = StringUtils.replace(url, " ", "%20");
		URL urlObj;
		HttpURLConnection conn = null;
		try {
			Log.d(ForBossUtils.class.getName(), "Start downloading image "+url+" as file:"+fileName);
			//create connection
			urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setDoInput(true);
			conn.setUseCaches(true);
			conn.setConnectTimeout(20000);
			conn.connect();
			final InputStream is = conn.getInputStream();

			//save stream to local
			final FileOutputStream fos = contextWrapper.openFileOutput(fileName, Context.MODE_PRIVATE);
			int read = 0;
			final byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
			}
			is.close();
			fos.flush();
			fos.close();
		} catch (final IOException e) {
			throw e;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static Bitmap loadBitmapFromUrl(final String url) {
		if (url == null || url == "") {
			return null;
		}
		URL urlObj;
		HttpURLConnection conn = null;
		Bitmap bm = null;
		try {
			//create connection
			urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setDoInput(true);
			conn.setUseCaches(true);
			conn.setConnectTimeout(20000);
			conn.connect();
			final InputStream is = conn.getInputStream();
			bm = BitmapFactory.decodeStream(is);
			is.close();
		} catch (final IOException e) {
			Log.e(ForBossUtils.class.getName(), "Unable to load bitmap from url=#" + url + "#", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return bm;
	}

	/**
	 * This method loads a bitmap from internal storage
	 * @param fileName
	 * @param contextWrapper the activity where this method is called
	 * @return the bitmap
	 * @throws FileNotFoundException
	 */
	public static Bitmap loadBitmapFromInternalStorage(final String fileName, final ContextWrapper contextWrapper) {
		try {
			final FileInputStream fis = contextWrapper.openFileInput(fileName);
			final Bitmap bm = BitmapFactory.decodeStream(fis);
			return bm;
		} catch (final OutOfMemoryError e) {
			Log.d(ForBossUtils.class.getName(), "Out of mem: " + fileName);
			e.printStackTrace();
			return null;
		} catch (final FileNotFoundException e) {
			Log.d(ForBossUtils.class.getName(), "File not found: " + fileName);
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap loadBitmapFromAssets(String imagePath, Context context) {
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			is = context.getAssets().open(imagePath);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			Log.e(ForBossUtils.class.getName(), "Unalbe to get input stream for assets image", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(ForBossUtils.class.getName(), "Unalbe to close input stream for assets image", e);
				}
				is = null;
			}
		}
		return bitmap;
	}

	public static Bitmap makeSquare(final Bitmap rectangle) {
		if (rectangle == null) {
			return null;
		}
		Bitmap square = rectangle;
		if (rectangle.getHeight() > rectangle.getWidth()) {
			square = Bitmap.createBitmap(rectangle, 0, (rectangle.getHeight() - rectangle.getWidth()) / 2, rectangle.getWidth(), rectangle.getWidth());
		} else if (rectangle.getWidth() > rectangle.getHeight()){
			square = Bitmap.createBitmap(rectangle, (rectangle.getWidth() - rectangle.getHeight()) / 2, 0, rectangle.getHeight(), rectangle.getHeight());
		}
		return square;
	}

	//the code come from:
	//	http://ruibm.com/?p=184
	public static Bitmap makeRounded(final Bitmap bitmap, final int pixels) {
		if (bitmap == null) {
			return bitmap;
		}

		final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * Show an alert dialog with message and "Close" button
	 * @param context
	 * @param message
	 */
	public static void alert(final Context context, final String message) {
		alert(context, message, null);
	}
	public static void alert(final Context context, final String message, final Handler finishHandler) {
		final AlertDialog.Builder	builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		.setPositiveButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
				if (finishHandler != null) finishHandler.sendEmptyMessage(0);
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private static ProgressDialog progressDialog = null;
	public static void alertProgress(final Context context, final String message) {
		dismissProgress(context);

		progressDialog = new ProgressDialog(context) {
			@Override
			public void onBackPressed() {};
		};
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public static void dismissProgress(final Context context) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	private static final DateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	public static final boolean sameDay(final Date aDate, final Date otherDate) {
		if (aDate == null && otherDate == null) {
			return true;
		}
		if (aDate == null || otherDate == null) {
			return false;
		}

		return YYYYMMDD.format(aDate).equals( YYYYMMDD.format(otherDate) );
	}

	public static final void addView(final ViewGroup parent, final View child) {
		if (parent == null || child == null) {
			return;
		}

		if (child.getParent() == null) {
			parent.addView(child);
		}
	}

	public static final void removeView(final ViewGroup parent, final View child) {
		if (parent == null || child == null) {
			return;
		}

		if (child.getParent() == parent) {
			parent.removeView(child);
		}
	}

	public static int convertDpToPixel(final int dp, final Context context) {
		return Math.round( dp * (context.getResources().getDisplayMetrics().densityDpi / 160f) );
	}
	
	public static int convertPixelToDp(int px, Context context) {
		return Math.round(px * 160f /context.getResources().getDisplayMetrics().densityDpi);
	}


	public static boolean isNetworkAvailable(Context context) {
		final ConnectivityManager connectivityManager
		= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	private static ResourceBundle config = ResourceBundle.getBundle("configurations");

	public static String getConfig(final String key) {
		return config.getString(key);
	}

	public static boolean getProductionalEnvironment() {
		return true;
	}

	private static String getDayOfMonthSuffix(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int n = calendar.get(Calendar.DAY_OF_MONTH);
		if (n < 1 || n > 31) {
			throw new IllegalArgumentException("illegal day of month: " + n);
		}
		if (n >= 11 && n <= 13) {
			return "th";
		}
		switch (n % 10) {
		case 1:  return "st";
		case 2:  return "nd";
		case 3:  return "rd";
		default: return "th";
		}
	}

	public static boolean isFileExisting(final String filename, final Context context) {
		final File file = context.getFileStreamPath(filename);
		return file.exists();
	}

	public static void copyAssetFile(final String src, final String dst) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		final AssetManager assets = ForBossApplication.getAppContext().getAssets();
		try {
			in = assets.open(src);
			out = new FileOutputStream(dst);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch(final Exception e) {
			Log.e(ForBossUtils.class.getName(), "Copy file failed because of ", e);
		}

	}

	private static void copyFile(final InputStream in, final OutputStream out) throws IOException {
		final byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	public static String[] getAssetListFromConfig(final String configKey) {
		final String configString = getConfig(configKey);
		final String[] splitted = StringUtils.split(configString, ",");
		return splitted;
	}


	public static void get(final String url, final Context context, final HandlerWithToken taskFinishedHandler) {
		if (!isNetworkAvailable(context)) {
			return;
		}
		final GetUrlRunnable getUrlRunnable = new GetUrlRunnable();
		getUrlRunnable.setUrl(url);
		getUrlRunnable.setTaskFinishedHandler(taskFinishedHandler);
		final Thread thread = new Thread(getUrlRunnable);
		thread.start();
	}

	public static class HandlerWithToken extends Handler {
		private final int token;
		public HandlerWithToken(final int token) {
			this.token = token;
		}
		public int getToken() {
			return token;
		}
	}

	private static class GetUrlRunnable implements Runnable {
		private String url;
		private HandlerWithToken taskFinishedHandler;


		public String getUrl() {
			return url;
		}


		public void setUrl(final String url) {
			this.url = url;
		}


		public HandlerWithToken getTaskFinishedHandler() {
			return taskFinishedHandler;
		}


		public void setTaskFinishedHandler(final HandlerWithToken taskFinishedHandler) {
			this.taskFinishedHandler = taskFinishedHandler;
		}


		@Override
		public void run() {
			// reference: http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
			final HttpClient httpclient = new DefaultHttpClient();
			final HttpGet httpget = new HttpGet(url);
			Log.i(this.getClass().getName(), "Attempt to get from URL:" + url);

			try {
				final HttpResponse response = httpclient.execute(httpget);
				final InputStream input = response.getEntity().getContent();
				final BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
				final String json = reader.readLine();
				final JSONTokener tokener = new JSONTokener(json);
				try {
					final JSONObject finalResult = new JSONObject(tokener);

					if (taskFinishedHandler != null)  {
						final Message message = taskFinishedHandler.obtainMessage();
						message.obj = finalResult;
						taskFinishedHandler.sendMessage(message);
					}

				} catch (final JSONException e) {
					Log.e("ForBossUtils", e.toString());
				}
				finally {
					if (input != null) {
						input.close();
					}
				}

			} catch (final ClientProtocolException e) {
				Log.e(ForBossUtils.class.getName(), e.toString());
				e.printStackTrace();
			} catch (final IOException e) {
				Log.e(ForBossUtils.class.getName(), e.toString());
				e.printStackTrace();
			}
		}
	}

	public static void recycleBitmapOfImage(final ImageView img, final String tag) {
		Bitmap oldBm = null;
		if (img.getTag() instanceof Bitmap) {
			oldBm = (Bitmap) img.getTag();
		} else if (img.getTag() instanceof Map) {
			oldBm = (Bitmap) ((Map)img.getTag()).get("bm");
		}
		if (oldBm != null) {
			img.setImageBitmap(null);
			img.setTag(null);
			oldBm.recycle();
			Log.d(ForBossUtils.class.getName(), "...........Recycle bitmap for " + tag + "..........");
		}
	}

	public static String getBaseApi() {		
		return config.getString("API_URL");
	}

	public static String joinArray(final Object[] arr, String separator) {
		if (separator == null) {
			separator = ", ";
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	public static class Validation {

		public static boolean isValidEmailAddress(final String emailAddress) {
			if (emailAddress == null || emailAddress.length() == 0) {
				return false;
			}
			final String emailRegex = "\\A(\\w[\\w._%+\\-]*\\w|\\w)@\\w[\\w.\\-]*\\.\\w+\\Z";
			if (!emailAddress.matches(emailRegex)) {
				return false;
			}

			final String[] noneEmailRegexes = { "\\A.*[._%+\\-]{2,}.*\\Z",
					"\\A[^@]{65,}@[^@]+\\Z", "\\A[^@]+@[^@]{256,}\\Z" };
			for (final String nonEmailRegex : noneEmailRegexes) {
				if (emailAddress.matches(nonEmailRegex)) {
					return false;
				}
			}
			return true;
		}

		public static boolean isValidPassword(final String password) {
			// password's length must >= 6
			if (password == null || password.length() < 6) {
				return false;
			}

			// contains at least 1 alphabet character
			final Pattern alphabetPattern = Pattern.compile("[a-zA-Z]");
			final Matcher alphabetMatcher = alphabetPattern.matcher(password);
			if (!alphabetMatcher.find()) {
				return false;
			}

			// contains at least 1 numberical character
			final Pattern numbericPattern = Pattern.compile("[0-9]");
			final Matcher numbericMatcher = numbericPattern.matcher(password);
			if (!numbericMatcher.find()) {
				return false;
			}

			return true;
		}

		public static boolean isValidPhoneNumber(String phoneNumber) {
			if (phoneNumber == null || phoneNumber.length() == 0) {
				return false;
			}
			return phoneNumber.matches("\\A\\+?[0-9.-]{5,20}\\Z");
		}

		public static boolean isValidCMND(String cmnd) {
			if (cmnd == null || cmnd.length() == 0) {
				return false;
			}
			return cmnd.matches("\\A\\d{5,20}\\Z");
		}
	}

	public static void setTypefaceForViewTree(View root, Typeface typeface) {
		if (!setTypefaceForView(root, typeface) && root instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) root;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				setTypefaceForViewTree(child, typeface);
			}
		}
	}
	public static boolean setTypefaceForView(View view, Typeface typeface) {
		if (view instanceof TextView) {
			((TextView)view).setTypeface(typeface);
		} else if (view instanceof EditText) {
			((EditText)view).setTypeface(typeface);
		} else {
			return false;
		}
		return true;
	}


	public static class Storage {
		public static String SHARED_PREFERENCES_KEY_REGISTERED = "registered?";
		public static SharedPreferences getSharedPreferences(Context context) {
			return context.getSharedPreferences("forboss2", Activity.MODE_PRIVATE);
		}
	}
	
	public static class UI {
		public static void initHomeButton(final Activity activity) {
			View buttonHome = activity.findViewById(R.id.buttonHome);
			buttonHome.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(activity, FeatureSelectingActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					activity.startActivity(intent);
				}
			});
		}
		
		public static void closeApp(Activity activity) {
			Intent intent = new Intent(activity, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			ForBossUtils.putBundleData("shouldCloseApp", true);
			activity.startActivity(intent);
		}
	}
}
