package com.innotech.imap_taxi.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.FragmentPacket;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.TarifAccept;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.Tarifs;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManagerTaxometr;
import com.innotech.imap_taxi.datamodel.Tarif;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;
import org.json.JSONException;

public class Main_taxometr extends FragmentActivity {
    PowerManager mgr;
    PowerManager.WakeLock wakeLock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_for_taxometr_fragment);
		ContextHelper.getInstance().setCurrentContext(this);
//        mgr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//        wakeLock = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, "IMAPWakeLock");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.wtf("TAXOMETER", "Create!!!");

    }

	@Override
	public void onBackPressed() {
		//// TODO Auto-generated method stub
        FragmentTransactionManagerTaxometr.getInstance().back();

		//super.onBackPressed();
	}

    @Override
    protected void onStart() {
        super.onStart();
        FragmentTransactionManagerTaxometr.getInstance().initializationFragmentTransaction(this);

    }

    @Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		ContextHelper.getInstance().setCurrentContext(this);
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FragmentTransactionManagerTaxometr.getInstance().remove(this);
        super.onSaveInstanceState(outState);
        if (!Tarifs.str.equals(""))
            outState.putString("tarifsClass", Tarifs.str);
        if (TarifAccept.tar != null)
            outState.putString("tarifAccept", TarifAccept.tar.toString());
        outState.putInt("currFragment", FragmentTransactionManagerTaxometr.getInstance().getId());
        Log.wtf("TAX", "SAVE");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            if (savedInstanceState.containsKey("tarifsClass"))
                Tarifs.str = savedInstanceState.getString("tarifsClass");
            if (savedInstanceState.containsKey("tarifAccept")){
                try {
                    TarifAccept.tar = new Tarif(savedInstanceState.getString(("tarifAccept")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//
        }
    }

    @Override
    protected void onResume() {
//        wakeLock.acquire();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        wakeLock.release();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.wtf("TAXOMETER", "DESTROY!!!");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.wtf("TAX", "CONFIG");

//

    }
}
