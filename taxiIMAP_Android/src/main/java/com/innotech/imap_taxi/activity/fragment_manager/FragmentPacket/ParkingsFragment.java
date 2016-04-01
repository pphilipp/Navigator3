package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.ParkingsAdapter;
import com.innotech.imap_taxi.core.StateObserver;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.model.parking;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.packet.DriverParkingPositionResponce;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.network.packet.RegisterOnTaxiParkingResponce;
import com.innotech.imap_taxi.network.packet.TaxiParkingStatisticResponce;
import com.innotech.imap_taxi.network.packet.TaxiParkingsResponce;
import com.innotech.imap_taxi.network.packet.UnRegisterOnTaxiParkingResponce;
import com.innotech.imap_taxi.utile.PlaySound;
import com.innotech.imap_taxi3.R;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParkingsFragment extends FragmentPacket {

    //sort order constants
    private static final int SORT_AZ = 1;
    private static final int SORT_ZA = 2;
    private static final int SORT_DISTANCE = 3;
    private static final int SORT_QUEUE = 4;

    //default sort order
    private int currentSortOrder = SORT_AZ;

    private final String TAG = "parking_tag";
    ServerData serv;
    AlertDialog aDialog;
    int arg;
    Boolean send;
    private ListView listView_parkings;
    private EditText searchET;
    private Button refresh;
    private Button back;
    private Button off;
    private ImageView search;
    private String parkingName;
    private int uid, position, all, lastPosition = 0;
    private boolean registered;
    private List<parking> parkings;
    private ParkingsAdapter mAdapter;
    private Typeface t;

    private BaseAdapter mAdapter1 = new BaseAdapter() {

        @Override
        public int getCount() {
            return parkings.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parking, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.parkingName);
                holder.drivers = (TextView) convertView.findViewById(R.id.driversOnParking);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String park = "";
            if (parkings.get(position).getDriverNames() != ""){
                holder.drivers.setText(parkings.get(position).getDriverNames());
                holder.drivers.setVisibility(View.VISIBLE);
            }
            else {
                holder.drivers.setVisibility(View.GONE);
            }
            holder.name.setText(parkings.get(position).getParkingName() + ", " + parkings.get(position).getCount());
            if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext()).getString(UserSettingActivity.KEY_TEXT_SIZE, "")) != 0) {
                holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext()).getString(UserSettingActivity.KEY_TEXT_SIZE, "")) + 14);
            }
            return convertView;
        }
    };


    static class ViewHolder{
        public TextView name, drivers;
    }

    public ParkingsFragment() {
        super(PARKINGS);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.parkings_fragment_new, container, false);

        Log.d(TAG, "parking onCreate");

        parkings = new ArrayList<>();
        mAdapter = new ParkingsAdapter(parkings);

        registerListeners();
        refresh();

        t = Typeface.createFromAsset(ContextHelper.getInstance()
                        .getCurrentContext().getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");


        listView_parkings = (ListView) myView.findViewById(R.id.listView_parkings);
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.parking_search, null);
        searchET = (EditText) headerView.findViewById(R.id.searchBox);
        searchET.setTypeface(t);

        //perform filtering on search
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                if (ConnectionHelper.getInstance().isConnected()) {
                    RequestHelper.getTaxiParkings();
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.getFilter().filter(s.toString());
                    }
                }, 500L);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        search = (ImageView) headerView.findViewById(R.id.searchImg);
        //show search dialog on click
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
        listView_parkings.addHeaderView(headerView);
        listView_parkings.setAdapter(mAdapter);

        listView_parkings.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                arg = arg2;
                ParkingCardFragment.setParking(parkings.get(arg - 1));
                FragmentTransactionManager.getInstance().openFragment(FragmentPacket.PARKING_CARD_FRAGMENT);
            }
        });
		/**/
        back = (Button) myView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwipeFragment.open = false;
                FragmentTransactionManager.getInstance().back();
            }
        });



        off = (Button) myView.findViewById(R.id.off_parking1);
        off.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                byte[] body4 = RequestBuilder.createUnRegisterOnTaxiParking(ServerData.getInstance().getPeopleID());
                byte[] data4 = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, serv.getSrvID(),
                        RequestBuilder.DEFAULT_DESTINATION_ID, serv.getGuid(), true, body4);
                ConnectionHelper.getInstance().send(data4);
                refresh();
            }
        });

        refresh = (Button) myView.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                parkings = new ArrayList<parking>();

                //            	ContextHelper.getInstance().runOnCurrentUIThread(
                //						new Runnable() {
                //							@Override
                //							public void run() {
                //								mAdapter.notifyDataSetChanged();
                //							}
                //						});

                refresh();

				/*try {
                    MultiPacketListener.getInstance().addListener(Packet.CSBALANCE_RESPONCE, new OnNetworkPacketListener() {

                        GetCSBalanceResponce getBalance;

                        @Override
                        public void onNetworkPacket(Packet packet) {
                            getBalance = (GetCSBalanceResponce) packet;
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    balance.setText(getBalance.getSum());
                                }
                            };

                            ContextHelper.getInstance().runOnCurrentUIThread(r);
                            UIData.getInstance().setBalance(getBalance.getSum());
                            Log.d("KVEST_TAG", getBalance.getSum());
                            MultiPacketListener.getInstance().removeListeners(Packet.CSBALANCE_RESPONCE);
                        }
                    });

                    byte[] body = RequestBuilder.createGetBalanceData(ServerData.getInstance().getPeopleID());
                    byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
                            RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body);
                    ConnectionHelper.getInstance().send(data);
                } catch (NullPointerException e) {
                    Toast.makeText(ContextHelper.getInstance().getCurrentContext(), "Вы не подключены к серверу!", Toast.LENGTH_LONG).show();
                    MultiPacketListener.getInstance().removeListeners(Packet.CSBALANCE_RESPONCE);
                }*/
            }
        });

		/*register = (Button) myView.findViewById(R.id.register_on_parking);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        Log.d(TAG, "onCreateView");

        return myView;
    }

    /**
     * method shoes dialog with sort choice
     */
    private void showSearchDialog() {
        Context mContext = ContextHelper.getInstance().getCurrentContext();

        final Dialog searchDialog = new Dialog(mContext);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        //define container view for dialog
        View dialogView = inflater.inflate(R.layout.search_dialog, null);

        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.search_dialog);
        searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //define, decorate and set onClicks for all buttons
        Button sortByNameAZ = (Button) searchDialog.findViewById(R.id.by_name_a_z);
        Button sortByNameZA = (Button) searchDialog.findViewById(R.id.by_name_z_a);
        Button sortByDistance = (Button) searchDialog.findViewById(R.id.by_distance);
        Button sortByQueue = (Button) searchDialog.findViewById(R.id.by_queue);

        //set Tags
        sortByNameAZ.setTag(SORT_AZ);
        sortByNameZA.setTag(SORT_ZA);
        sortByDistance.setTag(SORT_DISTANCE);
        sortByQueue.setTag(SORT_QUEUE);

        //setUp fonts
        sortByNameAZ.setTypeface(t);
        sortByNameZA.setTypeface(t);
        sortByDistance.setTypeface(t);
        sortByQueue.setTypeface(t);

        //get screen width
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;




        searchDialog.show();

        //adjust dialog size
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(searchDialog.getWindow().getAttributes());
        //get dialog width
        DisplayMetrics dm = new DisplayMetrics();
        searchDialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dialogHeight = dm.heightPixels;
        int dialogWidth = dm.widthPixels;

        lp.height = dialogHeight;
        lp.width = width;
        searchDialog.getWindow().setAttributes(lp);
        searchDialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);

        //setUp clicks
        OnClickListener sortBtnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSortOrder = (int) v.getTag();
                sortParkingsList(currentSortOrder);
                searchDialog.dismiss();
            }
        };

        sortByNameAZ.setOnClickListener(sortBtnListener);
        sortByNameZA.setOnClickListener(sortBtnListener);
        sortByDistance.setOnClickListener(sortBtnListener);
        sortByQueue.setOnClickListener(sortBtnListener);


    }

    private void sortParkingsList(int sortOrder) {
        switch (sortOrder) {
            case SORT_AZ:
                Collections.sort(parkings);
                break;
            case SORT_ZA:
                Collections.sort(parkings, parking.orderParkingsByNameZA);
                break;
            case SORT_DISTANCE:
                //TODO add this sort. Problem is that object parking doesn`t have field distance
                break;
            case SORT_QUEUE:
                Collections.sort(parkings,parking.orderParkingsByQueue);
                break;
        }
        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        refresh();
    }

    private void registerListeners() {
        MultiPacketListener.getInstance().addListener(Packet.TAXI_PARKINGS_RESPONCE, new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                TaxiParkingsResponce pack = (TaxiParkingsResponce) packet;

                parkings = new ArrayList<parking>();

                for (int i = 0; i < pack.uid.length; i++) {
                    parkings.add(new parking(pack.uid[i], pack.parkingName[i], pack.parkingRegion[i], pack.responseDate));
                }

                byte[] body2 = RequestBuilder.createBodyGetDriverParkingPosition(serv.getPeopleID());
                byte[] data2 = RequestBuilder.createSrvTransfereData(
                        RequestBuilder.DEFAULT_CONNECTION_TYPE, serv.getSrvID(),
                        RequestBuilder.DEFAULT_DESTINATION_ID, serv.getGuid(), true,
                        body2);
                ConnectionHelper.getInstance().send(data2);
                byte[] body3 = RequestBuilder.createGetTaxiParkingStatistic(serv.getPeopleID());
                byte[] data3 = RequestBuilder.createSrvTransfereData(
                        RequestBuilder.DEFAULT_CONNECTION_TYPE, serv.getSrvID(),
                        RequestBuilder.DEFAULT_DESTINATION_ID, serv.getGuid(), true,
                        body3);
                ConnectionHelper.getInstance().send(data3);
                Log.i(TAG, "goted TAXI_PARKINGS_RESPONCE " + Arrays.toString(pack.parkingName) + " " + Arrays.toString(pack.uid)); //ok
            }
        });
        Log.i(TAG, "register listener REGISTER_ON_TAXI_PARKING_RESPONCE");

        MultiPacketListener.getInstance().addListener(Packet.REGISTER_ON_TAXI_PARKING_RESPONCE, new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                final RegisterOnTaxiParkingResponce pack = (RegisterOnTaxiParkingResponce) packet;
                Log.i(TAG, "goted REGISTER_ON_TAXI_PARKING_RESPONCE " + pack.toString());

                uid = pack.getParkingUID();
                parkingName = pack.getParkingName();
                position = pack.getNumberInQueue();

                registered = true;

                byte[] body3 = RequestBuilder.createGetTaxiParkingStatistic(serv.getPeopleID());
                byte[] data3 = RequestBuilder.createSrvTransfereData(
                        RequestBuilder.DEFAULT_CONNECTION_TYPE, serv.getSrvID(),
                        RequestBuilder.DEFAULT_DESTINATION_ID, serv.getGuid(), true,
                        body3);
                ConnectionHelper.getInstance().send(data3);

                //            	NavigatorMenuActivity.open = false;
                //
                //            	if (FragmentTransactionManager.getInstance().getId() == FragmentPacket.PARKINGS) {
                //            		FragmentTransactionManager.getInstance().back();
                //            	}
            }
        });

        MultiPacketListener.getInstance().addListener(Packet.DRIVER_PARKING_POSITION_RESPONCE, new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                DriverParkingPositionResponce pack = (DriverParkingPositionResponce) packet;
                Log.d(TAG, "DRIVER_PARKING_POSITION_RESPONCE " + pack.getRegisteredOnParking() + " " + pack.getUID() + " " + pack.getPosition());

                if (pack.getRegisteredOnParking() == 1) {
                    for (parking p : parkings) {
                        if (p.getUid() == pack.getUID()) {
                            uid = pack.getUID();
                            parkingName = p.getParkingName();
                            position = pack.getPosition();

                            registered = true;

                            byte[] body3 = RequestBuilder.createGetTaxiParkingStatistic(serv.getPeopleID());
                            byte[] data3 = RequestBuilder.createSrvTransfereData(
                                    RequestBuilder.DEFAULT_CONNECTION_TYPE, serv.getSrvID(),
                                    RequestBuilder.DEFAULT_DESTINATION_ID, serv.getGuid(), true,
                                    body3);
                            ConnectionHelper.getInstance().send(data3);
                            body3 = null;
                            data3 = null;
                        }
                    }
                }

            }
        });

        MultiPacketListener.getInstance().addListener(Packet.TAXI_PARKING_STATISTIC_RESPONCE, new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                TaxiParkingStatisticResponce pack = (TaxiParkingStatisticResponce) packet;
                Log.d(TAG, "TAXI_PARKING_STATISTIC_RESPONCE " + Arrays.toString(pack.uid) + " " + Arrays.toString(pack.driversCount));

                for (int i = 0; i < pack.uid.length; i++) {

                    for (int j = 0; j < parkings.size(); j++) {
                        if (pack.uid[i] == parkings.get(j).getUid()) {
                            parkings.get(j).setCount(pack.driversCount[i]);
                            parkings.get(j).setDriverNames(pack.driverCallSigns[i]);
                        }
                    }

                    if (uid == pack.uid[i]) {
                        all = pack.driversCount[i];
                    }
                }
                if (registered) {
                    ContextHelper.getInstance().runOnCurrentUIThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (lastPosition != 0 && position != lastPosition && position == 1) {
                                        //play music
                                        PlaySound.getInstance().play(R.raw.msg_pos_change);
                                    }
                                    lastPosition = position;
                                    SwipeFragment.parkings.setText(parkingName + ":" + position + "/" + all);
                                    StateObserver.getInstance().setParkingPosition(position);
                                }
                            });
                    registered = false;
                } else {
                    ContextHelper.getInstance().runOnCurrentUIThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    //mAdapter.notifyDataSetChanged();
                                    mAdapter.updateAdapter(parkings);
                                }
                            });
                }

            }
        });

        MultiPacketListener.getInstance().addListener(Packet.UNREGISTER_ON_TAXI_PARKING_RESPONCE, new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                UnRegisterOnTaxiParkingResponce pack = (UnRegisterOnTaxiParkingResponce) packet;
                Log.d(TAG, "UNREGISTER_ON_TAXI_PARKING_RESPONCE " + pack.getUnregistered());

                registered = false;

                ContextHelper.getInstance().runOnCurrentUIThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                SwipeFragment.parkings.setText("Стоянки");
                                StateObserver.getInstance().setParkingPosition(0);
                            }
                        });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        Log.d(TAG, "ParkingFragment onResume1");
        //registerListeners();
        refresh();
        Log.d(TAG, "ParkingFragment onResume2");
        //balance.setText(UIData.getInstance().getBalance());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Parking onPause");
    }

    private void refresh() {
        parkings = new ArrayList<parking>();

        serv = ServerData.getInstance();
        //registerListeners();
        if (ConnectionHelper.getInstance().isConnected()) {
            RequestHelper.getTaxiParkings();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sortParkingsList(currentSortOrder);
                ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateAdapter(parkings);
                    }
                });
            }
        }, 1000L);

    }
}
