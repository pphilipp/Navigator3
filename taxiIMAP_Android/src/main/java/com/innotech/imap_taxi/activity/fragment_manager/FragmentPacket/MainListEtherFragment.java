package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.OrdersAdapterDisp4;
import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;
import java.util.ArrayList;

/**
 * Created by philipp on 25.04.16.
 */
public class MainListEtherFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    private static final String LOG_TAG = MainListEtherFragment.class.getSimpleName();
    LinearLayout llNoEtherSecond;
    ListView lvOrders;
    OrdersAdapterDisp4 mAdapter;
    TextView etherTxt;
    Button btnBusy;
    Button btnOrder;
    Button ethear;
    Button btnTest;

    ArrayList<DispOrder4> mOrders;
    Context mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrders = new ArrayList<DispOrder4>();
        mAdapter = new OrdersAdapterDisp4(mOrders, mContext);
        mContext = ContextHelper.getInstance().getCurrentContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_menu_chapter_two_new, container, false);
        etherTxt = (TextView) v.findViewById(R.id.noEther);
        etherTxt.setTypeface(Typeface.createFromAsset(ContextHelper.getInstance()
                .getCurrentContext().getAssets(), "fonts/BebasNeueRegular.ttf"));

        Typeface menuTypeface = Typeface.createFromAsset(ContextHelper
                        .getInstance().getCurrentContext().getAssets(),
                "fonts/BebasNeueRegular.ttf");

        btnBusy = (Button) v.findViewById(R.id.btn_busy);
        btnBusy.setTypeface(menuTypeface);
        btnBusy.setOnClickListener(this);

        btnOrder = (Button) v.findViewById(R.id.btn_order);
        btnOrder.setTypeface(menuTypeface);
        btnOrder.setOnClickListener(this);

        ethear = (Button) v.findViewById(R.id.ethear_button);
        ethear.setTypeface(menuTypeface);
        ethear.setOnClickListener(this);

        btnTest = (Button) v.findViewById(R.id.test_test);
        isTestBtn();

        llNoEtherSecond = (LinearLayout) v.findViewById(R.id.noEtherLayout);

        lvOrders = (ListView) v.findViewById(R.id.etherList);
        lvOrders.setAdapter(mAdapter);
//        switchListView(mAdapter);

        lvOrders.setOnItemClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case : break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(LOG_TAG, "Open description " + position + "\n");
        EfirOrder.setOrderId(mOrders.get(position).orderID);
        FragmentTransactionManager.getInstance().openFragment(
                FragmentPacket.ORDER);
    }

//    public void switchListView(OrdersAdapterDisp4 mAdapter) {
//        if (mapEther != null && llNoEther != null && lvOrders != null
//                && llNoEtherSecond != null) {
//            if (mAdapter.getCount() > 0) {
//                mapEther.setVisibility(View.VISIBLE);
//                llNoEther.setVisibility(View.GONE);
//                lvOrders.setVisibility(View.VISIBLE);
//                llNoEtherSecond.setVisibility(View.GONE);
//            } else {
//                mapEther.setVisibility(View.GONE);
//                llNoEther.setVisibility(View.VISIBLE);
//                lvOrders.setVisibility(View.GONE);
//                llNoEtherSecond.setVisibility(View.VISIBLE);
//            }
//        }
//    }

    public void isTestBtn() {
        if (ServerData.getInstance().IS_TEST_BUILD)
            btnTest.setOnClickListener(this);
        else
            btnTest.setVisibility(View.GONE);

    }
}
