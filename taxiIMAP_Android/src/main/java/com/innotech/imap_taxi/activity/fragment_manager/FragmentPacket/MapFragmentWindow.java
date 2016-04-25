package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.packet.GetRoutesResponse;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.ping.MyLocationListener;
import com.innotech.imap_taxi3.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapFragmentWindow extends FragmentPacket implements OnClickListener {
	private static final String LOG_TAG = MapFragmentWindow.class.getSimpleName();
	private GoogleMap mMap;
	LatLng myLoc;
	MarkerOptions myLoMmarker;
	@Bind(R.id.btn_show_me) Button btnMe;
    @Bind(R.id.btn_zoom_in) ImageButton zoomIn;
	@Bind(R.id.btn_zoom_out)ImageButton zoomOut;
	boolean updateMyLoc;
	View myView = null;
    ArrayList<LatLng> lg;
    float lat;
	float lon;
    public static int orderId = -1;

	public MapFragmentWindow() {
		super(MAP);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(LOG_TAG, "onSaveInstanceState()");
		setUserVisibleHint(true);
	}

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
		Log.d(LOG_TAG, "onViewStateRestored()");
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreateView()");
        if (myView != null) {
			((ViewGroup)myView.getParent()).removeAllViews();
            return myView;
		}

			myView  = inflater.inflate(R.layout.map_fragment_new, container, false);
			ButterKnife.bind(this, myView);
			btnMe.setOnClickListener(this);
			zoomIn.setOnClickListener(this);
			zoomOut.setOnClickListener(this);

			routesAnswerPacketListenerImpl();

		return myView;
    }

	private void routesAnswerPacketListenerImpl() {
		MultiPacketListener.getInstance().addListener(
				Packet.GET_ROUTES_ANSWER,
				new OnNetworkPacketListener() {
					@Override
					public void onNetworkPacket(Packet packet) {
						GetRoutesResponse pack = (GetRoutesResponse) packet;
						Log.e("IMAP", "!!! goted GET_ROUTES_ANSWER "
								+ pack.orderId + " x:" + pack.geoX.size()
								+ " y:" + pack.geoY.size());

						if (pack.geoX.size()>0) {
							lg = new ArrayList<LatLng>();

							for (int i = 0; i < pack.geoY.size(); i++) {
								lg.add(new LatLng(pack.geoY.get(i), pack.geoX.get(i)));
							}

							ContextHelper.getInstance().runOnCurrentUIThread(
									new Runnable() {
										public void run() {
											updateMyLoc = false;
											drawRouteIfExist(lg);
										}
									});
						}
					}
				});
	}

	public void drawRouteIfExist(ArrayList<LatLng> l) {
		Log.d(LOG_TAG, "drawRouteIfExist()" + "\n"
				+ " size - " + l.size() + "\n"
				+ "first point - " + l.get(0).latitude + "\n"
				+ " " + l.get(0).longitude);

		mMap.addMarker(new MarkerOptions().position(l.get(0)).title("Начало")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

		mMap.addMarker(new MarkerOptions().position(l.get(l.size()-1)).title("Финиш")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		mMap.addPolyline(new PolylineOptions().addAll(l).color(Color.BLUE));
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l.get(0), 16), 2000, null);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("MyMap", "MapFragmentWindow onResume");

        try {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(50.4327633,30.597208))
                    .zoom(10)
                    .build();
            Log.d("MyMap", "CameraCreate");
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(cameraUpdate);

            myLoMmarker = new MarkerOptions().position(new LatLng(0, 0)).title("Это я");
            myLoMmarker.visible(true);
            mMap.addMarker(myLoMmarker);
        }
        catch (Exception e) {
            Log.d(LOG_TAG, "Map onResume create error = " + e.getMessage());
            e.printStackTrace();
        }

        if(orderId != -1){
            Log.wtf("Map", "requestRoute");
            RequestHelper.getRoutes(orderId);
        }

		updateMyLoc = true;

		if (mMap != null && ServerData.getInstance().gpsOrNetProv) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (updateMyLoc) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
						if (MyLocationListener.lat != 0.0
								&& MyLocationListener.lon != 0.0) {
							myLoc = new LatLng(
									MyLocationListener.lat,
									MyLocationListener.lon);
							myLoMmarker.position(myLoc);
							myLoMmarker.visible(true);

							ContextHelper.getInstance().runOnCurrentUIThread(
									new Runnable() {
										public void run() {
                                            mMap.clear();

                                            mMap.addMarker(myLoMmarker);
										}
									});
						}
					}
				}
			}).start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
        Log.wtf("Map", "onPause");
        updateMyLoc = false;
	}

    @Override
    public void onStop() {
        super.onStop();
        Log.wtf("Map", "onStop");

    }

    @Override
	public void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
        Log.d(LOG_TAG, "onDestroy()");
        myView = null;
        mMap = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_show_me:
				if (mMap != null && MyLocationListener.lat != 0.0 && MyLocationListener.lon != 0.0) {
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyLocationListener.lat,
							MyLocationListener.lon), 15), 1500, null);
					Marker marker = mMap.addMarker(new MarkerOptions().
							position(new LatLng(MyLocationListener.lat, MyLocationListener.lon)).
							draggable(false).
							title("Вы здесь").
							icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_green)));
					marker.showInfoWindow();
				} else {
					Toast.makeText(ContextHelper.getInstance().getCurrentContext(), "Идет поиск ...", Toast.LENGTH_SHORT).show();
				}break;
			case R.id.btn_zoom_in :
				if(mMap != null)
					mMap.animateCamera(CameraUpdateFactory.zoomIn());
				break;
			case R.id.btn_zoom_out :
				if(mMap != null)
					mMap.animateCamera(CameraUpdateFactory.zoomOut());
				break;
			default: break;
		}
	}
}
