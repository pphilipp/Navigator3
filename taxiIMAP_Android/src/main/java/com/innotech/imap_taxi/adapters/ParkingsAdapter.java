package com.innotech.imap_taxi.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innotech.imap_taxi.graph_utils.GraphUtils;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.model.parking;
import com.innotech.imap_taxi3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 10.11.2015.
 */
public class ParkingsAdapter extends BaseAdapter implements Filterable{

    private static final String TAG = "PARKING_ADAPTER_TAG";
    private List<parking> parkings;
    private Typeface t;


    public ParkingsAdapter(List<parking> parkings) {
        this.parkings = parkings;
        this.t = Typeface.createFromAsset(ContextHelper.getInstance()
                        .getCurrentContext().getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");
    }

    @Override
    public Filter getFilter() {




        Log.d(TAG, "getFilter parkings.size ()total = " + parkings.size());


        Filter parkingNameFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                String query = constraint.toString().toLowerCase();
                List<parking> filteredList = new ArrayList<>();
                Log.d(TAG, "count = " + parkings.size());
                for (parking currentParking : parkings) {
                    if (currentParking.getParkingName().toLowerCase().contains(query)) {
                        filteredList.add(currentParking);
                        Log.d(TAG, "match");
                    }

                }
                results.count = filteredList.size();
                results.values = filteredList;

                Log.d(TAG, "count = " + filteredList.size());

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, final FilterResults results) {

                ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                    @Override
                    public void run() {
                        updateAdapter((List<parking>) results.values);
                    }
                });
            }
        };

        return parkingNameFilter;
    }



    private static class ViewHolder {

        TextView parkingName;
        TextView amountOfDrivers;
        LinearLayout backgroundLayout;
        PaintDrawable p;

    }

    @Override
    public int getCount() {
        return this.parkings.size();
    }

    @Override
    public Object getItem(int position) {
        return this.parkings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateAdapter(List<parking> parkings) {
        this.parkings = parkings;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder = null;
        //common parking view
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) ContextHelper.getInstance().getCurrentContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parking_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.parkingName = (TextView) convertView.findViewById(R.id.parking_name_txt);
            viewHolder.amountOfDrivers = (TextView) convertView.findViewById(R.id.amount_of_drivers_txt);
            //set typefaces to textViews
            viewHolder.parkingName.setTypeface(t);
            viewHolder.amountOfDrivers.setTypeface(t);

            viewHolder.backgroundLayout = (LinearLayout) convertView.findViewById(R.id.parking_item_holder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.p = (PaintDrawable) GraphUtils.getOrderItemGradient(convertView);
        viewHolder.backgroundLayout.setBackground(viewHolder.p);
        viewHolder.parkingName.setText(parkings.get(position).getParkingName());
        int driversCount = (parkings.get(position) != null) ? parkings.get(position).getCount() : 0;
        viewHolder.amountOfDrivers.setText(String.valueOf(driversCount));




        return convertView;
    }
}
