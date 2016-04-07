package com.innotech.imap_taxi.utile;

import android.app.Application;

public class MyApplication extends Application {
	private static MyApplication instance;

	public static MyApplication getInstance(){
		if (instance == null)
			instance = new MyApplication();

		return instance;
	}
	private boolean isVisible = true;
	public void resumed(){
		isVisible = true;
	}
	public void paused(){
		isVisible = false;
	}

	public boolean isVisible(){
		return isVisible;
	}
}
