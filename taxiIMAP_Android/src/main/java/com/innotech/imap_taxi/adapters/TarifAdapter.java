package com.innotech.imap_taxi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.innotech.imap_taxi.datamodel.Tarif;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

public class TarifAdapter extends ArrayAdapter<Tarif> {

	Tarif[] data;
	public TarifAdapter(Context context, Tarif[] data) {
		super(context,R.layout.tarif_item , data);
		this.data=data;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//TODO error
		if (convertView==null){
			LayoutInflater inflater = (LayoutInflater) ContextHelper.getInstance().getCurrentContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(R.layout.tarif_item,null, false);
		}
		((TextView)convertView.findViewById(R.id.textView1)).setText("«"+data[position].tariffname+"»");
		((TextView)convertView.findViewById(R.id.textView3)).setText(data[position].pricetoseat+"");
		((TextView)convertView.findViewById(R.id.textView5)).setText(data[position].priceperkilometr+"");
		((TextView)convertView.findViewById(R.id.textView7)).setText(data[position].priceforwait+"");
		((TextView)convertView.findViewById(R.id.textView9)).setText(data[position].pricedowntime+"");
		if (position%2==0)
			convertView.setBackgroundResource(R.drawable.black_button);
		else
			convertView.setBackgroundResource(R.drawable.blue_button);
		convertView.setTag(data[position].toString());
		return convertView;
	}

}
