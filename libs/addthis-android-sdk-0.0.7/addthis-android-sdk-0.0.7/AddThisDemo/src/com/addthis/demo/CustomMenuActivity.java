/*
 * CustomMenuActivity.java
 * AddThis
 * 
 * Copyright 2010-2011 AddThis LLC. All rights reserved.
 * 
 */
package com.addthis.demo;

import java.util.ArrayList;

import com.addthis.core.AddThis;
import com.addthis.error.ATDatabaseException;
import com.addthis.error.ATSharerException;
import com.addthis.models.ATService;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Presents a custom share menu.
 * 
 * @author Jithin Roy
 * 
 */
public class CustomMenuActivity extends Activity {

	private ArrayList<ATService> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custommenu);
		try {
			//	We take the whole services and store it in ArrayList.
			mList = AddThis.getAllServices(this,false);
		} catch (ATDatabaseException e) {
			e.printStackTrace();
		}
		setupGrid();
	}

	private void setupGrid() {
		if (mList == null || mList.size() == 0)
			return;

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				onClickedItem(position);
			}
		});
	}

	private void onClickedItem(int position) {
		try {
			//	Start sharing to the selected service.
			AddThis.shareItem(this, mList.get(position).getCode(),
					HomeActivity.mUrl, HomeActivity.mShareTitle,
					HomeActivity.mShareDescription);
		} catch (ATDatabaseException e) {
			
			e.printStackTrace();
		} catch (ATSharerException e) {
			
			e.printStackTrace();
		}
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mList.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) { 
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(40, 40));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageBitmap(mList.get(position).getImage());
			return imageView;
		}

	}

}