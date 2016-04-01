package com.innotech.imap_taxi.network;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import com.innotech.imap_taxi.activity.NavigatorMenuActivity;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.LogHelper;
import com.innotech.imap_taxi3.R;

public class SocketService extends Service {

    static String host;
    static String port;
    static Context ctx;
    static OnConnectionEstablishedListener connectionEstablishedListener;

    public static void setConnectionEstablishedListener(OnConnectionEstablishedListener connectionEstablishedListen) {
        connectionEstablishedListener = connectionEstablishedListen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext());

            host = ServerData.getInstance().isMasterServer()
                    ? sharedPrefs.getString("prefHost", "")
                    : sharedPrefs.getString("prefHostSlave", "").equals("")
                    ? sharedPrefs.getString("prefHost", "")
                    : sharedPrefs.getString("prefHostSlave", "");

            port = ServerData.getInstance().isMasterServer()
                    ? sharedPrefs.getString("prefPort", "")
                    : sharedPrefs.getString("prefPortSlave", "").equals("")
                    ? sharedPrefs.getString("prefPort", "")
                    : sharedPrefs.getString("prefPortSlave", "");
            ctx = getApplicationContext();

            Log.d("onStartCommand ", host + " " + port);
        } catch (Exception e) {
            e.printStackTrace();
            Log.getStackTraceString(e);
//            Log.d("SocketServiceException", e.getMessage());
        }
        Log.d("tag-tag-tag", "onStartCommand_NotifServ");
        startService();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    private void startService() {

        Log.d("SS", "перед иконкой");
        Notification note = new Notification(R.drawable.ic_launcher,
                "ImapNavigator",
                System.currentTimeMillis());
        Intent intent = new Intent(this, NavigatorMenuActivity.class);
        //	intent.setAction("");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        note.setLatestEventInfo(this, "ImapNavigator",
                "ImapNavigator",
                pi);
        note.flags |= Notification.FLAG_NO_CLEAR;

        Log.d("SS", "перед добавлением в бэкграунд");
        startForeground(133217, note);

        //try {

        Log.d("SS", "перед открытием коннекта");
        openConnection();
        /*} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
    }

    private void stop() {
        stopForeground(true);
        WatchSocket.disconnect();
        Log.e("tag-tag-tag", "service stoped");
    }

    // данный метод открыает соединение
    @SuppressLint("NewApi")
    public void openConnection()// throws InterruptedException
    {
        Log.d("tag-tag-tag", "openConnection_NotifServ");
        try {

            LogHelper.w_connection("перед экзекьютом");
            // WatchData - это класс, с помощью которого мы передадим параметры в
            // создаваемый поток
            // создаем новый поток для сокет-соединенияW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new WatchSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
            else
                new WatchSocket().execute();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.w("беда", e.getMessage());

            e.printStackTrace();
        }
    }

    public class LocalBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }
}