package com.innotech.imap_taxi.ping;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.LogHelper;

public class MyLocationListener implements LocationListener {

    public static double lat;
    public static double lon;
    public static int direction;
    public static byte speed;

    /*private GpsStatus gpsStatus;
    private GpsListener gpsLisnter;*/
    private LocationManager locationManager;

    @Override
    public void onLocationChanged(Location loc) {
        lat = loc.getLatitude();
        lon = loc.getLongitude();
        speed = (byte) loc.getSpeed();
        direction = 42;
        //	direction=(int) loc.getBearing();
        //System.out.println("onLocationChanged");
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(ContextHelper.getInstance().getCurrentContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        //ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.gps_indication).setBackgroundResource(R.drawable.no_gps_working);
        //PlaySound.getInstance().play(R.raw.msg_stat_neg);
        Log.d("NavigationMenuAct", "onProviderDisabled");

//        LogHelper.w_gps("onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(ContextHelper.getInstance().getCurrentContext(), "Gps Enabled",	Toast.LENGTH_SHORT).show();
        //ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.gps_indication).setBackgroundResource(R.drawable.gps_working);
    /*	locationManager = (LocationManager) ContextHelper.getInstance().getCurrentContext().getSystemService(Context.LOCATION_SERVICE);
		gpsStatus = locationManager.getGpsStatus(null);
		locationManager.addGpsStatusListener(gpsLisnter);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2*1000, 0, this);
	*/
        Log.d("NavigationMenuAct","onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

/*	class GpsListener implements GpsStatus.Listener{
		@Override
		public void onGpsStatusChanged(int event) {
			try{
				Iterable<GpsSatellite> sats = gpsStatus.getSatellites();

				for (GpsSatellite sat:sats)
				{
					direction=(int) sat.getAzimuth();
				}
			}
			catch (Exception e){}
			gpsStatus = locationManager.getGpsStatus(gpsStatus);
		}
	}
*/

}