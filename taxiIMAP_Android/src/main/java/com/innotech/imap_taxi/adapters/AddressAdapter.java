package com.innotech.imap_taxi.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.innotech.imap_taxi.datamodel.Address;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

import java.util.List;

/**
 * Created by Sergey on 13.11.2015.
 */
public class AddressAdapter extends BaseAdapter {

    Context mContext;
    List<Address> addresses;
    ViewHolder viewHolder;

    public AddressAdapter(Context mContext, List<Address> addresses) {
        this.mContext = mContext;
        this.addresses = addresses;
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int position) {
        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ContextHelper.getInstance().getCurrentContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.address_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.addressTxt.setText(addresses.get(position).getStreetName() + ", " + addresses.get(position).getHouse());

        if (position != 0 && position != getCount() - 1) {
            viewHolder.routeImage.setImageResource(R.drawable.middle_route);
        }
        if (position == getCount() - 1) {
            viewHolder.routeImage.setImageResource(R.drawable.end_route);
        }
        if (position == 0) {
            viewHolder.routeImage.setImageResource(R.drawable.first_point_route);
        }

        return convertView;
    }

    public void upDateList(List<Address> route) {
        this.addresses = route;
        this.notifyDataSetChanged();
    }

    private class ViewHolder {

        private TextView addressTxt;
        private Typeface mTypeface;
        private ImageView routeImage;


        private ViewHolder(View v) {
            addressTxt = (TextView) v.findViewById(R.id.address_txt);
            mTypeface = Typeface.createFromAsset(ContextHelper.getInstance()
                    .getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");
            addressTxt.setTypeface(mTypeface);
            routeImage = (ImageView) v.findViewById(R.id.route_img);
        }
    }
}
