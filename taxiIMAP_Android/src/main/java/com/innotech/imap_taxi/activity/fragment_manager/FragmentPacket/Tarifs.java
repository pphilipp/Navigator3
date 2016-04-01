package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManagerTaxometr;
import com.innotech.imap_taxi.adapters.TarifAdapter;
import com.innotech.imap_taxi.datamodel.Tarif;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

public class Tarifs extends FragmentPacket {
    public Tarifs() {
        super(TAXOMETR_TARIFS);
    }

    static ListView lv;
    static GridView gv;
    public static String str = "";
    View myView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        myView = inflater.inflate(R.layout.tarifs, null, false);
        View v = myView.findViewById(R.id.spisok_tarifov);
        if ((ListView) v != null)
            lv = (ListView) v;
        else
            gv = (GridView) v;
        if(!str.equals(""))
            createTarifs(str);
        return myView;
    }


    public static void createTarifs(final String tar) {
        str = tar;
        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONArray ja = new JSONArray(str);
                    Tarif[] data = new Tarif[ja.length()];
                    for (int i = 0; i < ja.length(); i++)
                        data[i] = new Tarif(ja.getJSONObject(i));
                    TarifAdapter adapter = new TarifAdapter(ContextHelper.getInstance().getCurrentContext(), data);
                    OnItemClickListener onclick = new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {
                            TarifAccept.openTarif(arg1.getTag().toString());
                            FragmentTransactionManagerTaxometr.getInstance().openFragment(FragmentPacket.TAXOMETR_ACCEPT_TARIF);
                        }
                    };
                    if (lv != null) {
                        lv.setAdapter(adapter);
                        lv.setOnItemClickListener(onclick);
                    } else {
                        gv.setAdapter(adapter);
                        gv.setOnItemClickListener(onclick);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
