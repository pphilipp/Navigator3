package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.HouseAdapter;
import com.innotech.imap_taxi.adapters.StreetAdapter;
import com.innotech.imap_taxi.datamodel.Address;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.house;
import com.innotech.imap_taxi.datamodel.street;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.postRequests.ServiceConnection;
import com.innotech.imap_taxi3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 16.11.2015.
 * Fragment provides input of new address
 */
public class GetAddressFragment extends FragmentPacket {

    //Log tag
    private static final String TAG = "GET_ADDRESS_FRAG";

    public GetAddressFragment() {
        super(FragmentPacket.GET_ADDRESS);
    }

    private static Order currentOrder;

    //currently chosen address
    private street currentStreet;
    private String currentStreetID;
    private house currentHouse;
    private String currentHouseID;
    private static Address currentAddress;



    private static EditText streetInput;
    private EditText houseInput;
    private ImageView search;

    public static Address getCurrentAddress() {
        Address newAddress = currentAddress;
        currentAddress = null;
        return newAddress;
    }

    //address from/to image
    private ImageView point;

    private Context mContext;

    //typeface
    private Typeface mTypeface;

    //list of streets from server
    private List<street> streets = new ArrayList<>();
    //list of houses from server
    private List<house> houses = new ArrayList<>();

    //adapter for streetList
    private StreetAdapter mStreetAdapter;
    //adapter for houseList
    private HouseAdapter mHouseAdapter;
    //streets ListView
    private ListView streetListView;
    //houses gridView
    private GridView housesList;

    //cancel/create orders
    private Button cancel;
    private Button create;

    public static void setCurrentOrder(Order mOrder) {
        currentOrder = mOrder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.get_address_fragment,
                container, false);

        mTypeface = Typeface.createFromAsset(ContextHelper.getInstance()
                .getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");

        mContext = ContextHelper.getInstance().getCurrentContext();

        point = (ImageView) rootView.findViewById(R.id.point_image);
        point.setImageResource(getPointImageResource());

        streetInput = (EditText) rootView.findViewById(R.id.street_ET);
        streetInput.setTypeface(mTypeface);
        houseInput = (EditText) rootView.findViewById(R.id.house_ET);
        houseInput.setTypeface(mTypeface);

        streetListView = (ListView) rootView.findViewById(R.id.list_of_streets);
        mStreetAdapter = new StreetAdapter(mContext, streets);
        streetListView.setAdapter(mStreetAdapter);

        //post request service
        final ServiceConnection sc = new ServiceConnection(mContext);

        //refresh street list on text input
        streetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                streetListView.setVisibility(View.VISIBLE);
                houseInput.setEnabled(false);
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                final String query = "" + s;
                Thread streetThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        streets = sc.getStreet(query);
                        mStreetAdapter.upDateList(streets);
                    }
                });
                streetThread.start();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //on street been chosen, hide list of streets and set street name in input
        streetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentStreet = streets.get(position);
                currentStreetID = String.valueOf(streets.get(position).getId());
                streetInput.setText(currentStreet.getName());
                streetInput.setSelection(currentStreet.getName().length());
                houseInput.setEnabled(true);
                streets.clear();
                mStreetAdapter.upDateList(streets);
                streetListView.setVisibility(View.GONE);
                //houseInput.requestFocus();
            }
        });

        //define and setUp houses grid
        housesList = (GridView) rootView.findViewById(R.id.house_list);
        mHouseAdapter = new HouseAdapter(mContext, houses);
        housesList.setAdapter(mHouseAdapter);

        //fetch houses from server on input change
        houseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                housesList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final String query = "" + s;
                Thread streetThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "streetID = " + currentStreetID + ", query = " + query);
                        houses = sc.getStreetHouse(currentStreetID, query);
                        mHouseAdapter.upDateList(houses);
                    }
                });
                streetThread.start();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //setUp on houseChosen behavior
        housesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentHouse = houses.get(position);
                houseInput.setText(currentHouse.getValue());
                currentHouseID = houses.get(position).getId();
                houseInput.setSelection(currentHouse.getValue().length());
                houses.clear();
                housesList.setVisibility(View.GONE);
                mHouseAdapter.upDateList(houses);
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        //set action for create/cancel buttons (and define them)
        cancel = (Button) rootView.findViewById(R.id.cancel);
        cancel.setTypeface(mTypeface);
        create = (Button) rootView.findViewById(R.id.create_btn);
        create.setTypeface(mTypeface);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearForm();
                getCurrentAddress();
                FragmentTransactionManager.getInstance().back();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrderData();
                clearForm();
                FragmentTransactionManager.getInstance().back();
            }
        });


        return rootView;
    }

    private void clearForm() {
        streetInput.setText("");
        houseInput.setText("");
        streetListView.setVisibility(View.GONE);
        housesList.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        point.setImageResource(getPointImageResource());
    }

    /**
     * Save new address into route
     */
    private void saveOrderData() {
        currentAddress = new Address();
        currentAddress.setStreetName(currentStreet.getName());
        currentAddress.setStreetID(currentStreetID);
        currentAddress.setHouse(currentHouse.getValue());
        currentAddress.setHouseID(currentHouseID);
    }


    private int getPointImageResource() {
        if (currentOrder == null || currentOrder.getStreet() == null || currentOrder.getStreet().equals("")) {
            return R.drawable.add_address_from;
        }
        return R.drawable.add_address_to;
    }
}
