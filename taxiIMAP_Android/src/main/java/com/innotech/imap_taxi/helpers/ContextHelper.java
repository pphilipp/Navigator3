package com.innotech.imap_taxi.helpers;

import com.innotech.imap_taxi.activity.NavigatorMenuActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

/**
 * Created with IntelliJ IDEA.
 * User: u27
 * Date: 8/29/13
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContextHelper {

	private static ContextHelper instance;

	private Context currentContext;

	private ContextHelper() {

	}

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

	public void restartApp(){
		finishActivity();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				getCurrentActivity().startActivity(new Intent(getCurrentContext(),NavigatorMenuActivity.class));		
			}
		}, 800L);
		
	}	
}
