package com.innotech.imap_taxi.utile;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.innotech.imap_taxi.activity.MyImapApp;
import com.innotech.imap_taxi.activity.NavigatorMenuActivity;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationService extends Service {

	private static NotificationManager nm;
	static List<String> notificationList = new ArrayList<String>();

	//int i = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			sendNotif(intent.getStringExtra("notif_text"), intent.getStringExtra("notif_text_details"), intent.getStringExtra("notif_orderid"));
			return super.onStartCommand(intent, flags, startId);
		} catch (Exception e) {
			e.printStackTrace();
			return START_STICKY_COMPATIBILITY;
		}
	}

	void sendNotif(String text, String message_detailed, String orderid) {
		String message = "";
		String action = "";

		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar c = Calendar.getInstance();
		String time = dateFormat.format(c.getTime());

		if (text.equals("FindDriver1")) {
			message = " Автопоиск 1";
			action = "f1";
			notificationList.add(orderid+"f12");
		} else if (text.equals("FindDriver2")) {
			message = " Aвтопоиск 2";
			action = "f2";
			notificationList.add(orderid+"f12");
		} else if (text.equals("FindDriver3")) {
			message = " Заказ в эфире";
			action = "ef";
			notificationList.add(orderid+"f3");
		} else if (text.equals("SendedByDispatcher")) {
			message = " Направленный заказ";
			action = "sbd";
			notificationList.add(orderid);
		} else if (text.equals("notif")) {
			message = " " + message_detailed;
			action = "upd";
			notificationList.add(orderid);
		} else if (text.equals("notifSMS")) {
			message = " " + message_detailed;
			action = "sms";
			notificationList.add(orderid);
		} else {
			return;
		}

		Notification notif = new Notification(R.drawable.ic_launcher, message + " (" + message_detailed + ")", System.currentTimeMillis());
		notif.defaults=Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
		notif.when=System.currentTimeMillis();

		Intent intent = new Intent(this, NavigatorMenuActivity.class);
		intent.setAction(action);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		Log.e("TEST_TAG", "added notif - " + notificationList.size() + " orderid - " + orderid);

		PendingIntent pIntent = PendingIntent.getActivity(this, notificationList.size(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//i++;
		// PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		notif.setLatestEventInfo(this, time + message, message_detailed, pIntent);

		notif.flags |= Notification.FLAG_AUTO_CANCEL;

		// отправляем
		//nm.notify(notificationList.size(), notif);
		nm.notify(1, notif);
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public static boolean sendNotific(String mess, String mess_details, String orderid){
		if (!MyImapApp.getInstance().isVisible()){
			Intent intent = new Intent(ContextHelper.getInstance().getCurrentActivity(), NotificationService.class);
			intent.putExtra("notif_text", mess);
			intent.putExtra("notif_text_details", mess_details);
			intent.putExtra("notif_orderid", orderid);
			ContextHelper.getInstance().getCurrentActivity().startService(intent);
			return true;
		}
		return false;
	}

	public static void cancelNotif(int orderid, String from){
		int index = notificationList.indexOf(orderid + from);
		Log.e("TEST TAG", "cancelNotif index - " + (index+1) + " orderid " + orderid + " from - " + from);
		if (index!=-1)
			nm.cancel(index+1);
		try{
			notificationList.set(index, "");
		}
		catch (Exception e){}
	}
}