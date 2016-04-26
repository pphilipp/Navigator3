package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.graph_utils.GraphUtils;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

/**
 * Created by philipp on 25.04.16.
 */
public class MainMenuFragment extends Fragment implements View.OnClickListener{
    private static final int RESULT_SETTINGS = 1;
    public static boolean reCon = false;
    Button btnOrders;
    Button btnPrelim;
    Button myOwnOrder;
    Button parkings;
    Button btnMap;
    Button tvBalance;
    Button connection;
    Button btnCloseConnection;
    Button btnPrefs;
    Button btnTaxoMetr;
    Button btnSendCrash;
    Button btnBalance;
    TextView tvVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_menu_chapter_one_new,container,false);
        btnOrders = (Button) v.findViewById(R.id.btnOrders);




        Typeface menuTypeface = Typeface.createFromAsset(ContextHelper
                        .getInstance().getCurrentContext().getAssets(),
                "fonts/BebasNeueRegular.ttf");
        btnOrders.setTypeface(menuTypeface);
        btnOrders.setOnClickListener(this);

        btnPrelim = (Button) v.findViewById(R.id.btnPrelim);
        btnPrelim.setTypeface(menuTypeface);
        btnPrelim.setOnClickListener(this);

        myOwnOrder = (Button) v.findViewById(R.id.btn_do_my_order);
        myOwnOrder.setTypeface(menuTypeface);
        myOwnOrder.setOnClickListener(this);

        parkings = (Button) v.findViewById(R.id.parkingsButton);
        parkings.setTypeface(menuTypeface);
        parkings.setOnClickListener(this);

        btnMap = (Button) v.findViewById(R.id.btnmap);
        btnMap.setTypeface(menuTypeface);
        btnMap.setOnClickListener(this);

        tvBalance = (Button) v.findViewById(R.id.balanceButton);
        tvBalance.setTypeface(menuTypeface);

        tvVersion = (TextView) v.findViewById(R.id.version);

        connection = (Button) v.findViewById(R.id.connection);
        connection.setTypeface(menuTypeface);
        connection.setOnClickListener(this);
        isReConnect();

        btnCloseConnection = (Button) v.findViewById(R.id.exit);
        btnCloseConnection.setTypeface(menuTypeface);
        btnCloseConnection.setOnClickListener(this);

        btnPrefs = (Button) v.findViewById(R.id.btn_prefs);
        btnPrefs.setTypeface(menuTypeface);
        btnPrefs.setOnClickListener(this);

        btnTaxoMetr = (Button) v.findViewById(R.id.btn_taxometr);
        btnTaxoMetr.setTypeface(menuTypeface);
        btnTaxoMetr.setOnClickListener(this);

        btnSendCrash = (Button) v.findViewById(R.id.sendCrash);
        btnSendCrash.setOnClickListener(this);
        btnSendCrash.setTypeface(menuTypeface);

        btnBalance = (Button) v.findViewById(R.id.balanceButton);


        initButtonSyle();



        return v;
    }

    private void initButtonSyle() {
        final PaintDrawable p = (PaintDrawable) GraphUtils.buttonStyle(connection);
        btnSendCrash.setBackground(p);
        connection.setBackground(p);
        btnOrders.setBackground(p);
        myOwnOrder.setBackground(p);
        btnMap.setBackground(p);
        btnBalance.setBackground(p);
        btnPrelim.setBackground(p);
        btnTaxoMetr.setBackground(p);
        parkings.setBackground(p);
        btnPrefs.setBackground(p);
        btnCloseConnection.setBackground(p);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connection:
//                connectionClick();
                break;
            case R.id.btnOrders:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ORDERS);
                break;
            case R.id.btn_do_my_order:
//                myOwnOrdersClick();
                break;
            case R.id.btnmap:
                MapFragmentWindow.orderId = -1;
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.MAP);
                break;
            case R.id.btnPrelim:
//                CurrentOrdersFragment
//                        .displayOrders(CurrentOrdersFragment.STATE_PRE);
//                FragmentTransactionManager.getInstance()
//                        .openFragment(CURRENTORDERS);
                break;
            case  R.id.parkingsButton:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.PARKINGS);
                break;
            case R.id.btn_busy:
//                busyClick();
                break;
            case R.id.btn_taxometr:
//                taxometrClick();
                break;
            case R.id.btn_order:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ORDERS);
                break;
            case R.id.ethear_button:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ETHEAR);
                break;
            case R.id.test_test:
//                testClick();
                break;
            case R.id.exit:
//                exitClick();
                break;
            case R.id.btn_prefs:
                ContextHelper
                        .getInstance()
                        .getCurrentActivity()
                        .startActivityForResult(
                                new Intent(ContextHelper.getInstance()
                                        .getCurrentContext(),
                                        UserSettingActivity.class),
                                RESULT_SETTINGS);
                break;
            case R.id.sendCrash :
//                showConfirmToast(false, "Address ");
                break;
            default:break;
        }

    }

    public void isReConnect() {
        if (reCon) {
            connection.performClick();
            reCon = false;
        }
    }
}
