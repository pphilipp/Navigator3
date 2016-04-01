package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManagerTaxometr;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.packet.GetTaximeterRates;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi3.R;

public class ClassiTarifov extends FragmentPacket {

    LinearLayout ll;

    public ClassiTarifov() {
        super(TAXOMETR_ClASSES_TARIF);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View myView = inflater.inflate(R.layout.classes_tarif, null, false);
        ll = (LinearLayout) myView.findViewById(R.id.linLay);
        //String js = ContextHelper.getInstance().getSharedPreferences().getString("classes", "");
        byte[] body2 = RequestBuilder.createGetTaximeterRates(ServerData.getInstance().getPeopleID());
        byte[] data2 = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
                RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body2);
        ConnectionHelper.getInstance().send(data2);

        MultiPacketListener.getInstance().addListener(Packet.TAXIMETER_RATES, new OnNetworkPacketListener() {

            @Override
            public void onNetworkPacket(final Packet packet) {
                ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {

                    @Override
                    public void run() {
                        GetTaximeterRates pack = (GetTaximeterRates) packet;
                        JSONArray ja = pack.getClasses();
                        if (ja.length() != 0) {
                            ll.removeAllViewsInLayout();
                            LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);
                            JSONObject job;
                            for (int i = 0; i < ja.length(); i++) {
                                try {
                                    job = ja.getJSONObject(i);
                                    Button b = new Button(ContextHelper.getInstance().getCurrentContext());
                                    b.setText(job.getString("autoclass"));
                                    b.setTextColor(getResources().getColor(android.R.color.white));
                                    b.setLayoutParams(lp);
                                    if (i % 2 == 0)
                                        b.setBackgroundResource(R.drawable.black_button);
                                    else
                                        b.setBackgroundResource(R.drawable.blue_button);
                                    b.setTag(job.getJSONArray("tariffs"));
                                    b.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View arg0) {
                                            FragmentTransactionManagerTaxometr.getInstance().openFragment(FragmentPacket.TAXOMETR_TARIFS);
                                            Tarifs.createTarifs(arg0.getTag().toString());
                                        }
                                    });
                                    ll.addView(b);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });
        return myView;
    }
}
