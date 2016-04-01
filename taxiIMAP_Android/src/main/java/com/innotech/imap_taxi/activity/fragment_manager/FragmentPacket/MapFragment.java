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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.innotech.imap_taxi.activity.NavigatorMenuActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.packet.GetRoutesResponse;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.ping.MyLocationListener;
import com.innotech.imap_taxi3.R;

public class MapFragment extends FragmentPacket {

	private GoogleMap mMap;
	LatLng myLoc;
	MarkerOptions myLoMmarker;

	Button btnMe, btnBack;
    ImageButton zoomIn, zoomOut;

	boolean updateMyLoc;
	View myView = null;

    ArrayList<LatLng> lg;

    float lat, lon;
    public static int orderId = -1;

	public MapFragment() {
		super(MAP);
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.wtf("Map", "onCreateView");

        if (myView!=null)
		{
			((ViewGroup)myView.getParent()).removeAllViews();
            return myView;
		}
		try{
			myView  = inflater.inflate(R.layout.map_fragment_new, container, false);


			btnMe = (Button) myView.findViewById(R.id.btn_show_me);
//			btnBack = (Button) myView.findViewById(R.id.btn_back);
            zoomIn = (ImageButton) myView.findViewById(R.id.btn_zoom_in);
            zoomOut = (ImageButton) myView.findViewById(R.id.btn_zoom_out);

//			btnBack.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					FragmentTransactionManager.getInstance().back();
//				}
//			});

			btnMe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mMap != null && MyLocationListener.lat != 0.0 && MyLocationListener.lon != 0.0) {
//						mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(MyLocationListener.lat, MyLocationListener.lon)));
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyLocationListener.lat, MyLocationListener.lon),17), 1500, null);
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(MyLocationListener.lat,MyLocationListener.lon)).draggable(false).title("Вы"));
					} else {
						Toast.makeText(ContextHelper.getInstance().getCurrentContext(), "Идет поиск ...", Toast.LENGTH_SHORT).show();
					}
				}
			});

            zoomIn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mMap != null){
                        mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    }
                }
            });
            zoomOut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mMap != null){
                        mMap.animateCamera(CameraUpdateFactory.zoomOut());
                    }
                }
            });

			if (mMap == null) {
				mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(
						R.id.map)).getMap();
                mMap.setMyLocationEnabled(true);

				if (mMap != null) {

//					mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.6, 32)));
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.6, 32),6), 100, null);

                    mMap.getUiSettings().setZoomControlsEnabled(false);

					myLoMmarker = new MarkerOptions().position(new LatLng(0, 0)).title("Это я");
					myLoMmarker.visible(false);
					mMap.addMarker(myLoMmarker);
				}
			}

			MultiPacketListener.getInstance().addListener(Packet.GET_ROUTES_ANSWER, new OnNetworkPacketListener() {
				@Override
				public void onNetworkPacket(Packet packet) {
					GetRoutesResponse pack = (GetRoutesResponse) packet;
					Log.e("IMAP", "!!! goted GET_ROUTES_ANSWER " + pack.orderId + " x:" + pack.geoX.size() + " y:" + pack.geoY.size());

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
		catch (Exception e){
            e.printStackTrace();
            Log.getStackTraceString(e);
//            Log.e("MApFragment Exception", e.getMessage());
        }
		return myView;
	}

	public void drawRouteIfExist(ArrayList<LatLng> l) {

		Log.v("ABC", "size - " + l.size() + "first point - " + l.get(0).latitude + " " + l.get(0).longitude);

		mMap.addMarker(new MarkerOptions().position(l.get(0)).title("Начало")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

		mMap.addMarker(new MarkerOptions().position(l.get(l.size()-1)).title("Финиш")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		mMap.addPolyline(new PolylineOptions().addAll(l).color(Color.BLUE));
//		mMap.moveCamera(CameraUpdateFactory.newLatLng(l.get(0)));
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l.get(0),16), 2000, null);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("myLogs", "MapFragment onResume");
		Log.d("catchOnResume", "fragId = Map");
        Log.wtf("Map", "onResume");
        if(orderId != -1){
            Log.wtf("Map", "requestRoute");
            RequestHelper.getRoutes(orderId);
        }
		updateMyLoc = true;

		if (mMap != null && ServerData.getInstance().gpsOrNetProv) {

			//Log.e("test", "map loc upd");

			new Thread(new Runnable() {

				@Override
				public void run() {

					while (updateMyLoc) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
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
        Log.wtf("Map", "onDestroy");
        myView = null;
        mMap = null;
	}

}
