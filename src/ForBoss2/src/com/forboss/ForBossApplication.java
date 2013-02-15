package com.forboss;

import android.app.Application;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class ForBossApplication extends Application {
	private static ForBossApplication instance;

	public ForBossApplication() {
		instance = this;
	}

	public static Context getAppContext() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
	}

	private static Display display;	
	public static Display getWindowDisplay() {
		return display;
	}

//	private static Typeface montserratTypeface;
//	public static Typeface getMontserratTypeface() {
//		if (montserratTypeface == null) {
//			montserratTypeface = Typeface.createFromAsset(ForBossApplication.getAppContext().getAssets(), "font/Montserrat-Regular.ttf");
//		}
//		return montserratTypeface;
//	}

	// support checking if the app is running in foreground
	// from here: http://stackoverflow.com/questions/3667022/android-is-application-running-in-background/5862048#5862048
	public static boolean isActivityVisible() {
		return activityVisible;
	}  
	public static void activityResumed() {
		activityVisible = true;
	}
	public static void activityPaused() {
		activityVisible = false;
	}
	private static boolean activityVisible;
}
