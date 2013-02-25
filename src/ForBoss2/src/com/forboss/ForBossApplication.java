package com.forboss;

import java.util.Locale;

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
	
	private static Locale locale = new Locale("vi");
	public static Locale getDefaultLocale() {
		return locale;
	}
}
