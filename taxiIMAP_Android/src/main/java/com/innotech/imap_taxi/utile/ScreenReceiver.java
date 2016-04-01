package com.innotech.imap_taxi.utile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.innotech.imap_taxi.activity.NavigatorMenuActivity;
import com.innotech.imap_taxi.helpers.ContextHelper;

public class ScreenReceiver extends BroadcastReceiver {

	//public static boolean wasScreenOn = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		/* if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
        	ContextHelper.getInstance().getCurrentActivity().moveTaskToBack(true);
            wasScreenOn = false;
        } else*/// if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			ContextHelper.getInstance().finishActivity();
        	Intent inten = new Intent(context, NavigatorMenuActivity.class);
        	inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(inten);
        	ContextHelper.getInstance().getCurrentContext().unregisterReceiver(this);
     //   	wasScreenOn = true;
      //  }
	}

}