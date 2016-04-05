package com.innotech.imap_taxi.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
/**
 * @class ContextHelper is singleton that
 * gives access to global context.
 * */

public class ContextHelper {
	private static ContextHelper instance;
	private Context currentContext;

	private ContextHelper() {}

	public static ContextHelper getInstance() {
		if (instance == null) {
			instance = new ContextHelper();
		}

		return instance;
	}

	public Context getCurrentContext() {
		return currentContext;
	}

	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}

	public Activity getCurrentActivity() {
		return (Activity) currentContext;
	}

	public void finishActivity() {
		getCurrentActivity().finish();
	}

	public void runOnCurrentUIThread(Runnable action) {
		getCurrentActivity().runOnUiThread(action);
	}

	public SharedPreferences getSharedPreferences() {
		return getCurrentActivity().getPreferences(Context.MODE_PRIVATE);
	}

	public void restartApp(){}
}
