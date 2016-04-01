package com.innotech.imap_taxi.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class SendLogs {

	private static final String TAG = "LOG_FILE";
	private static final String PATH = "/imap_android/";

	Context mContext;
	Activity mActivity;

	public SendLogs() {
		super();
		this.mContext = ContextHelper.getInstance().getCurrentContext();
		this.mActivity = ContextHelper.getInstance().getCurrentActivity();
	}

	public void sendLog() {

		Date modDate;
		int fileNumber;
		File file = new File(Environment.getExternalStorageDirectory(), PATH);
		File[] crashLogFiles = file.listFiles();
		modDate = new Date(crashLogFiles[0].lastModified());
		fileNumber = 0;
		for (int i = 1; i < crashLogFiles.length; i++)
			if (modDate.before((new Date(crashLogFiles[i].lastModified())))) {
				modDate = new Date(crashLogFiles[i].lastModified());
				fileNumber = i;
			}
		String body = "";
		String subject = "imap_Navigator3_crashReport";
		String adress = "sergey.grechukha@innotech-ua.com";
		try {
			body = getStringFromFile(crashLogFiles[fileNumber]);
		} catch (Exception e) {
			Log.d(TAG, "file text to sendError = " + e.getMessage());
			e.printStackTrace();
		}

		if (!body.equals("")) {
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
					Uri.fromParts("mailto", adress, null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
			emailIntent.putExtra(Intent.EXTRA_TEXT, body);
			mContext.startActivity(Intent.createChooser(emailIntent,
					"Send email..."));
		}

	}

	public static String convertStreamToString(InputStream is) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}
		return total.toString();
	}

	public static String getStringFromFile(File mFile) throws Exception {
		File fl = mFile;
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		// Make sure you close all streams.
		fin.close();
		return ret;
	}
}
