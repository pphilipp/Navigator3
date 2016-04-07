package com.innotech.imap_taxi.utile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.innotech.imap_taxi.helpers.ContextHelper;

public class AlertDHelper {
	static AlertDialog.Builder builder;
	static AlertDialog dialog;

	public static void showDialogOk(String message) {
		try{
			if (dialog == null){
				builder = new AlertDialog.Builder(ContextHelper.getInstance().getCurrentActivity());
				builder.setMessage(message)
				.setTitle("Уведомление")
				.setPositiveButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});

				dialog = builder.create();
				dialog.setCancelable(false);
			}
			else
				dialog.setMessage(message);
			dialog.show();
		}
		catch (Exception e){e.printStackTrace();}
	}

}
