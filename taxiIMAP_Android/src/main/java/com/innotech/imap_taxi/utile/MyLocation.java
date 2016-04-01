package com.innotech.imap_taxi.utile;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

public class MyLocation {
    private static Context context;
    private LocationManager locationManager;
    private LocationListener listener;
    private String provider;

    public MyLocation(Context context) {
        super();

        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "включите GPS на устройстве", Toast.LENGTH_LONG).show();
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
                locationManager.requestLocationUpdates(provider, 0, 10.0F, listener);
            } else {
                Toast.makeText(context, "включите GPS на устройстве", Toast.LENGTH_LONG).show();
            }
        } else {
            provider = LocationManager.GPS_PROVIDER;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10.0F, listener);
        }
    }

    public Point getGPSCoordinats() {
        Location location;
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            Point gpsPoint = new Point();

            gpsPoint.set((int) (location.getLatitude() * 1000000), (int) (location.getLongitude() * 1000000));
            return gpsPoint;
        }
        return null;
    }

    public double getX() {
        Location location;
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            return location.getLongitude();
        }

        return 0;
    }

    public double getY() {
        Location location;
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            return location.getLatitude();
        }

        return 0;
    }

    public double getXFromNetwork() {
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            return location.getLongitude();
        }
        return 0;
    }

    public double getYFromNetwork() {
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            return location.getLatitude();
        }
        return 0;
    }

    public double getAccuracy() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            return location.getAccuracy();
        }

        return 0;
    }

    public boolean isProvideEnabledGPS() {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isProvideEnabledNetwork() {

        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
