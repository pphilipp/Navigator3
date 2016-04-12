package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import org.json.JSONException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManagerTaxometr;
import com.innotech.imap_taxi.datamodel.Tarif;
import com.innotech.imap_taxi3.R;

public class TarifAccept extends FragmentPacket{
    public static Tarif tar = null;

	public TarifAccept() {
		super(TAXOMETR_ACCEPT_TARIF);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	static View myView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		myView = inflater.inflate(R.layout.tarif_accept, null, false);
        if(tar != null){
            openTarif(tar.jo.toString());
        }
		return myView;
	}

	public static void openTarif(String j){
		try {
			tar = new Tarif(j);
			((TextView)myView.findViewById(R.id.tarif_name)).setText("ТАРИФ «" + tar.tariffname +"»");
			((TextView)myView.findViewById(R.id.tarif_pos)).setText(tar.pricetoseat+"");
			((TextView)myView.findViewById(R.id.tarif_km)).setText(tar.priceperkilometr+"");
			((TextView)myView.findViewById(R.id.tarif_wait)).setText(tar.priceforwait+"");
			((TextView)myView.findViewById(R.id.tarif_down)).setText(tar.pricedowntime+"");
			myView.findViewById(R.id.button_YES).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					TaxometrFragment.setTarif(tar);
					FragmentTransactionManagerTaxometr.getInstance().openFragment(FragmentPacket.TAXOMETR);
				}
			});
			myView.findViewById(R.id.button_NO).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					FragmentTransactionManagerTaxometr.getInstance().back();
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
