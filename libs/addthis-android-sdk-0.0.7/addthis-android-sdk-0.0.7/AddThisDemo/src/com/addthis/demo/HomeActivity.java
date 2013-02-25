/*
 * HomeActivity.java
 * AddThis
 * 
 * Copyright 2010-2011 AddThis LLC. All rights reserved.
 * 
 */
package com.addthis.demo;

import com.addthis.core.AddThis;
import com.addthis.core.Config;
import com.addthis.error.ATDatabaseException;
import com.addthis.error.ATSharerException;
import com.addthis.models.ATShareItem;
import com.addthis.ui.views.ATButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class HomeActivity extends Activity {

	private WebView mWebView = null;

	public static final String mShareTitle = "Check";
	public static final String mShareDescription = "Loremipsum";
	public static final String mUrl = "http://www.addthis.com";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeview);

		// Configure the library.
		// Replace the appids and other keys for Facebook/Twitter/Twitpic with
		// your own id/keys. The library will show a warning toast if you use
		// the id/keys shipped with this demo.

		// Tells the library to use Facebook Connect for Facebook sharing.
	//	Config.configObject().setShouldUseFacebookConnect(true);

		// Tells the library to use Twitter OAuth for Twitter sharing.
		Config.configObject().setShouldUseTwitterOAuth(true);

		// Visit http://www.facebook.com/developers/createapp.php to create your
		// own Facebook App id and replace the one given here.
		Config.configObject().setFacebookAppId("175729095772478");

		// Visit http://dev.twitter.com/apps/new to create your own keys for
		// Twitter and replace the corresponding values given below. You should
		// select 'browser' as the application type when asked. Note that the
		// callback URL doesn't need to be an existing URL, but it should be
		// entered.
		Config.configObject().setTwitterConsumerKey("LGv5u6rSHT5apS5pQZFDw");
		Config.configObject().setTwitterConsumerSecret(
				"BPyxJc0plzxm3Z5io4CDsTKK8tO2AJq00rocEukX6I");
		Config.configObject().setTwitterCallbackUrl(
				"http://addthis.com/mobilesdk/twittertesting");
		
		//	If you also want to share an image through Twitter, which will
		//	internally use TwitPic, you should obtain key from http://dev.twitpic.com/
		//	and then provide your TwitPic API key to the library.
		Config.configObject().setTwitPicApiKey(
				"45149651ec391a4e2b8135b43a63346b");

		// Loads the AddThis site to the webview.
		mWebView = (WebView) findViewById(R.id.webview1);
		mWebView.loadUrl("http://www.addthis.com");
		try {
			AddThis.setFavoriteMenuServices(this, "facebook","twitter");
		} catch (ATDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// The AddThis button placed in the xml layout is taken
		// and the share items are added to it.
		ATButton btn = (ATButton) findViewById(R.id.addthisButton);
		ATShareItem item = new ATShareItem(mUrl, mShareTitle, mShareDescription);
		btn.setItem(item);
	}

	/**
	 * Called when the Create your own menu is clicked.
	 * This will start a new activity and list the all services
	 * using a custom menu.
	 * 
	 * @param v
	 */
	public void onClickedCustomMenu(View v) {
		Intent intent = new Intent(this, CustomMenuActivity.class);
		startActivity(intent);
	}

	/**
	 * Called when Custom Button is clicked.
	 * This will call the AddThis menu.
	 * 
	 * @param v
	 */
	public void onClickedCustomButton(View v) {
		AddThis.presentAddThisMenu(this, mUrl, mShareTitle, mShareDescription);
	}

	/**
	 * Called when Image Sharing is clicked.
	 * This will present the AddThis menu for image
	 * sharing.
	 * 
	 * @param v
	 */
	public void onImageCustomButton(View v) {
		Bitmap image = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.icon);

		AddThis.presentAddThisMenu(this, mShareTitle, mShareDescription, image);
	}

	/**
	 * Initiate sharing to Facebook.
	 * 
	 * @param v
	 */
	public void onClickedFacebook(View v) {
		try {
			AddThis.shareItem(this, "facebook", mUrl, mShareTitle,
					mShareDescription);
		} catch (ATDatabaseException e) {
			e.printStackTrace();
		} catch (ATSharerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initiate sharing to Email.
	 * 
	 * @param v
	 */
	public void onClickedEmail(View v) {
		try {
			AddThis.shareItem(this, "email", mUrl, mShareTitle,
					mShareDescription);
		} catch (ATDatabaseException e) {
			e.printStackTrace();
		} catch (ATSharerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initiate sharing to Digg.
	 * 
	 * @param v
	 */
	public void onClickedDigg(View v) {
		try {
			AddThis.shareItem(this, "digg", mUrl, mShareTitle,
					mShareDescription);
		} catch (ATDatabaseException e) {
			e.printStackTrace();
		} catch (ATSharerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initiate sharing to Twitter.
	 * 
	 * @param v
	 */
	public void onClickedTwitter(View v) {
		try {
			AddThis.shareItem(this, "twitter", mUrl, mShareTitle,
					mShareDescription);
		} catch (ATDatabaseException e) {
			e.printStackTrace();
		} catch (ATSharerException e) {
			e.printStackTrace();
		}

	}

}