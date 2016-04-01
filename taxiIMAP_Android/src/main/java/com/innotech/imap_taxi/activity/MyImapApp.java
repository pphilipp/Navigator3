package com.innotech.imap_taxi.activity;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.innotech.imap_taxi.network.SocketService;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

public class MyImapApp extends Application {

	private static final String TAG = "ERROR_TAG";
	public static MyImapApp instance;
	
	private UncaughtExceptionHandler defaultUEH;



	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

            stopService(new Intent(MyImapApp.this,SocketService.class));
			String mess = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + " "
					+ thread.getName().toString() + stack2string(ex);

			File sdCardRoot = Environment.getExternalStorageDirectory();
			File yourDir = new File(sdCardRoot, "imap_android");
			yourDir.mkdirs();

			createFile(mess);
			Log.d(TAG, "error = " + ex.getMessage());
			// re-throw critical exception further to the os (important)
			defaultUEH.uncaughtException(thread, ex);

		}
	};

	public void createFile(String str) {
		FileWriter fWriter;
		try {

			Calendar c = Calendar.getInstance();
			String mf = "crash_imap-" + c.get(Calendar.DATE) + "."
					+ (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR)
					+ "-" + c.get(Calendar.HOUR_OF_DAY) + "."
					+ c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND)
					+ ".txt";
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/imap_android/" + mf;

			Log.d("filename", path);

			fWriter = new FileWriter(path);
			fWriter.write(str);
			fWriter.flush();
			fWriter.close();
		} catch (Exception e) {
			Log.d("error", e.getMessage());
		}
	}

	public String stack2string(Throwable e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "\r\n" + sw.toString() + "";
		} catch (Exception e2) {
			return "bad stack2string";
		}
	}

	public MyImapApp() {
		defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

		// setup handler for uncaught exception
		//Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);

	}

	public static MyImapApp getInstance(){
		if (instance==null)
			instance=new MyImapApp();
		return instance;
	}

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(getApplicationContext(), new Crashlytics());
    }

    private boolean isVisible=true;
	public void resumed(){
		isVisible=true;
	}
	public void paused(){
		isVisible=false;
	}
	public boolean isVisible(){
		return isVisible;
	}
}
