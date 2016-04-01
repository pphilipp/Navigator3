package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.model.parking;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi3.R;

/**
 * Created by Sergey on 25.11.2015.
 */
public class ParkingCardFragment extends FragmentPacket {

    private static final String TAG = "PARKING_CARD_FRAGMENT";
    private static parking currentParking;
    private TextView parkingNameTxt;
    private TextView amountOfDriversTxt;
    private TextView listOfDriversTxt;
    private Button addMeBtn;
    private Button deleteMeBtn;
    private Typeface t;

    private ServerData serv;

    public ParkingCardFragment() {
        super (PARKING_CARD_FRAGMENT);
    }

    public static void setParking(parking parking) {
        currentParking = parking;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parkingCardView = inflater.inflate(R.layout.parking_card_fragment, container, false);

        //define all view elements
        parkingNameTxt = (TextView) parkingCardView.findViewById(R.id.parking_name_txt);
        amountOfDriversTxt = (TextView) parkingCardView.findViewById(R.id.driver_amount_txt);
        listOfDriversTxt = (TextView) parkingCardView.findViewById(R.id.list_of_drivers);

        addMeBtn = (Button) parkingCardView.findViewById(R.id.let_me_in_btn);
        deleteMeBtn = (Button) parkingCardView.findViewById(R.id.let_me_out_btn);

        //set typefaces
        t = Typeface.createFromAsset(ContextHelper.getInstance()
                        .getCurrentContext().getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");
        parkingNameTxt.setTypeface(t);
        amountOfDriversTxt.setTypeface(t);
        listOfDriversTxt.setTypeface(t);
        addMeBtn.setTypeface(t);
        deleteMeBtn.setTypeface(t);

        updateFragmentState();

        //set buttons clickListeners
        addMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContextHelper.getInstance().getCurrentContext());
                builder.setMessage("Вы уверены, что хотите стать на стоянку " + currentParking.getParkingName() + "?");
                builder.setCancelable(true);
                builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            serv = ServerData.getInstance();
                            byte[] body = RequestBuilder.createBodyRegisterOnTaxiParking(currentParking.getUid(), serv.getNick(), serv.getPeopleID());
                            byte[] data = RequestBuilder.createSrvTransfereData(
                                    RequestBuilder.DEFAULT_CONNECTION_TYPE, serv.getSrvID(),
                                    RequestBuilder.DEFAULT_DESTINATION_ID, serv.getGuid(), true,
                                    body);
                            ConnectionHelper.getInstance().send(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Handler().postDelayed(new Runnable() {

                            public void run() {
                                updateFragmentState();
                            }

                        }, 1000L);

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog aDialog = builder.create();
                aDialog.show();
            }
        });

        deleteMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] body4 = RequestBuilder.createUnRegisterOnTaxiParking(ServerData.getInstance().getPeopleID());
                byte[] data4 = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, serv.getSrvID(),
                        RequestBuilder.DEFAULT_DESTINATION_ID, serv.getGuid(), true, body4);
                ConnectionHelper.getInstance().send(data4);
                FragmentTransactionManager.getInstance().back();
            }
        });

        return parkingCardView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFragmentState();
    }

    private void updateFragmentState() {

        //text color

        if (currentParking != null) {

            serv = ServerData.getInstance();
            String driversList = currentParking.getDriverNames() != null ? currentParking.getDriverNames() : "Empty";
            //set up buttons visibility. if driver is in list set deleteMeBtn visible and vice verse
            addMeBtn.setVisibility(driversList.contains(serv.getNick()) ? View.GONE : View.VISIBLE);
            deleteMeBtn.setVisibility(driversList.contains(serv.getNick()) ? View.VISIBLE : View.GONE);


            driversList = driversList.replace(serv.getNick(), "<font color='grey'>"+serv.getNick()+"</font>");
            Log.d(TAG, "driverList = " + driversList);
            Spanned test = Html.fromHtml(driversList);

            parkingNameTxt.setText(currentParking.getParkingName() != null ? currentParking.getParkingName() : "Empty");
            amountOfDriversTxt.setText(currentParking.getCount() != 0 ? "" + currentParking.getCount() : "" + 0);
            listOfDriversTxt.setText(test);
        }
    }
}
