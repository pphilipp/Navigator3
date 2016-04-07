package com.innotech.imap_taxi.ping;

import java.util.TimerTask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.widget.ImageView;
import com.innotech.imap_taxi.core.StateObserver;
import com.innotech.imap_taxi3.R;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.utile.MyLocation;

public class PingTask extends TimerTask {
	private MyLocation location;

	private Runnable switchGprsIndicator = new Runnable() {
		@Override
		public void run() {
			try{
				if(isDataConnectionAvailable(ContextHelper.getInstance().getCurrentContext())) {
                    StateObserver.getInstance().setNetwork(StateObserver.WORK);
//                    ((ImageView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication)).setImageResource(R.drawable.gprs_working);
				} else {
                    StateObserver.getInstance().setNetwork(StateObserver.NO_WORK);
//                    ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication)).setImageResource(R.drawable.no_gprs_working);
				}
			}
			catch (Exception e){}
		}
	};

	private Runnable switchGpsIndicator = new Runnable() {
		@Override
		public void run() {

		}
	};

	private Runnable switchServerIndicator = new Runnable() {
		@Override
		public void run() {
			try{
				if(ConnectionHelper.getInstance().isConnected()) {
//                    ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.server_working);
				        StateObserver.getInstance().setServer(StateObserver.WORK);
                } else {
//                    ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.no_server_working);
				        StateObserver.getInstance().setServer(StateObserver.NO_WORK);
                }
			}
			catch (Exception e){}
		}
	};

	public PingTask() {
		super();

		//location = new MyLocation(ContextHelper.getInstance().getCurrentContext());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ContextHelper.getInstance().runOnCurrentUIThread(switchGprsIndicator);
		ContextHelper.getInstance().runOnCurrentUIThread(switchGpsIndicator);
		ContextHelper.getInstance().runOnCurrentUIThread(switchServerIndicator);
	}

	public static boolean isDataConnectionAvailable(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if(info == null)
			return false;

		return connectivityManager.getActiveNetworkInfo().isConnected();
	}
}
