package com.innotech.imap_taxi.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.FragmentPacket;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.TarifAccept;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.Tarifs;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManagerTaxometr;
import com.innotech.imap_taxi.datamodel.Tarif;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;
import org.json.JSONException;

public class MainTaxometr extends FragmentActivity {
    public static final String LOG_TAG = MainTaxometr.class.getSimpleName();
    PowerManager mgr;
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_for_taxometr_fragment);
        Log.d(LOG_TAG, "onCreate()");
        ContextHelper.getInstance().setCurrentContext(this);
//        mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//        wakeLock = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, "IMAPWakeLock");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentTransactionManagerTaxometr.getInstance().initializationFragmentTransaction(this);
        Log.d(LOG_TAG, "onStart()");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.d(LOG_TAG, "onResumeFragments()");
        ContextHelper.getInstance().setCurrentContext(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FragmentTransactionManagerTaxometr.getInstance().remove(this);
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
        if (!Tarifs.str.equals(""))
            outState.putString("tarifsClass", Tarifs.str);
        if (TarifAccept.tar != null)
            outState.putString("tarifAccept", TarifAccept.tar.toString());
        outState.putInt("currFragment", FragmentTransactionManagerTaxometr.getInstance().getId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState()");
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("tarifsClass"))
                Tarifs.str = savedInstanceState.getString("tarifsClass");
            if (savedInstanceState.containsKey("tarifAccept")) {
                try {
                    TarifAccept.tar = new Tarif(savedInstanceState.getString(("tarifAccept")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        wakeLock.acquire();
        Log.d(LOG_TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause()");
//        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged()");
    }
}