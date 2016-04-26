package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innotech.imap_taxi.adapters.OrdersAdapterDisp4;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.packet.GetRoutesResponse;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.utile.VerticalSeekBar;
import com.innotech.imap_taxi3.R;

import java.util.ArrayList;

/**
 * Created by philipp on 25.04.16.
 */
public class MainMapSwipeFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = MainMapSwipeFragment.class.getSimpleName();
    boolean updateMyLoc;
    ListView mapEther;
    ArrayList<LatLng> lg;
    OrdersAdapterDisp4 mAdapter;
    LinearLayout llNoEther;
    TextView mapNoEtherTxt;
    VerticalSeekBar zoomBar;
    Button btnZoomIn;
    Button btnZoomOut;
    GoogleMap mMap;
    ToggleButton toggleBtnHide;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_with_ether_fragment, container, false);
        mapEther = (ListView) v.findViewById(R.id.mapEther);
        mapEther.setAdapter(mAdapter);
        llNoEther = (LinearLayout) v.findViewById(R.id.noEtherLayout);
        mapNoEtherTxt = (TextView) v.findViewById(R.id.noEther);
        zoomBar = (VerticalSeekBar) v.findViewById(R.id.zoom_bar);
        btnZoomIn = (Button) v.findViewById(R.id.zoom_in);
        btnZoomOut = (Button) v.findViewById(R.id.zoom_out);
        toggleBtnHide = (ToggleButton) v.findViewById(R.id.hide_ether);


        btnZoomIn.setOnClickListener(this);


        Typeface tf = Typeface.createFromAsset(ContextHelper.getInstance()
                        .getCurrentContext().getAssets(),
                "fonts/BebasNeueRegular.ttf");
        mapNoEtherTxt.setTypeface(tf);
//        switchListView(mAdapter);
        zoomingReaction();
        hideTogleBtn();
        addMultiPacketListeners();

        return v;
    }

    private void addMultiPacketListeners() {
        MultiPacketListener.getInstance().addListener(
                Packet.GET_ROUTES_ANSWER,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        GetRoutesResponse pack = (GetRoutesResponse) packet;
                        Log.e("IMAP", "!!! goted GET_ROUTES_ANSWER "
                                + pack.orderId + " x:" + pack.geoX.size()
                                + " y:" + pack.geoY.size());

                        if (pack.geoX.size() > 0) {

                            lg = new ArrayList<LatLng>();
                            for (int i = 0; i < pack.geoY.size(); i++) {
                                lg.add(new LatLng(pack.geoY.get(i), pack.geoX
                                        .get(i)));
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


        MultiPacketListener.getInstance().addListener(
                Packet.GET_ROUTES_ANSWER,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        GetRoutesResponse pack = (GetRoutesResponse) packet;
                        Log.e("IMAP", "!!! goted GET_ROUTES_ANSWER "
                                + pack.orderId + " x:" + pack.geoX.size()
                                + " y:" + pack.geoY.size());

                        if (pack.geoX.size() > 0) {

                            lg = new ArrayList<LatLng>();
                            for (int i = 0; i < pack.geoY.size(); i++) {
                                lg.add(new LatLng(pack.geoY.get(i), pack.geoX
                                        .get(i)));
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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart()");

        if (mMap != null) {
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(new LatLng(48.6, 32), 6),
                    100, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zoom_in:
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
                break;
            case R.id.zoom_out:
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
                break;
        }
    }

    /** public void switchListView(OrdersAdapterDisp4 mAdapter) {
        if (mapEther != null && llNoEther != null && lvOrders != null
                && llNoEtherSecond != null) {
            if (mAdapter.getCount() > 0) {
                mapEther.setVisibility(View.VISIBLE);
                llNoEther.setVisibility(View.GONE);
                lvOrders.setVisibility(View.VISIBLE);
                llNoEtherSecond.setVisibility(View.GONE);
            } else {
                mapEther.setVisibility(View.GONE);
                llNoEther.setVisibility(View.VISIBLE);
                lvOrders.setVisibility(View.GONE);
                llNoEtherSecond.setVisibility(View.VISIBLE);
            }
        }
    } */


    public void drawRouteIfExist(ArrayList<LatLng> l) {

        Log.v("ABC", "size - " + l.size() + "first point - "
                + l.get(0).latitude + " " + l.get(0).longitude);

        mMap.addMarker(new MarkerOptions()
                .position(l.get(0))
                .title("Начало")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        mMap.addMarker(new MarkerOptions()
                .position(l.get(l.size() - 1))
                .title("Финиш")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addPolyline(new PolylineOptions().addAll(l).color(Color.BLUE));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(l.get(0)));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l.get(0),16),
        // 2000, null);
    }
    private void zoomingReaction() {
        if (mMap != null) {
            zoomBar.setMaximum((int) mMap.getMaxZoomLevel());
            zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            zoomBar.setProgressAndThumb((int) mMap.getCameraPosition().zoom);
        }
    }

    private void hideTogleBtn() {
        toggleBtnHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mapEther.setVisibility(View.GONE);
                    llNoEther.setVisibility(View.GONE);
                } else if (mAdapter.getCount() > 0) {
                    mapEther.setVisibility(View.VISIBLE);
                } else
                    llNoEther.setVisibility(View.VISIBLE);
            }
        });
    }
}
