package com.innotech.imap_taxi.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innotech.imap_taxi.datamodel.street;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

import java.util.List;

/**
 * Created by Sergey on 16.11.2015.
 */
public class StreetAdapter extends BaseAdapter {

    private final Typeface mTypeface;
    private Context mContext;
    private List<street> streets;

    public StreetAdapter(Context mContext, List<street> streets) {
        this.mContext = mContext;
        this.streets = streets;
        mTypeface = Typeface.createFromAsset(ContextHelper.getInstance()
                .getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");
    }

    private class ViewHolder{

        TextView streetNameTxt;

    }

    @Override
    public int getCount() {
        return streets.size();
    }

    @Override
    public street getItem(int position) {
        return streets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void upDateList(List<street> streets) {
        this.streets = streets;
        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) ContextHelper.getInstance().getCurrentContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.street_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.streetNameTxt = (TextView) convertView.findViewById(R.id.streetName_txt);
            viewHolder.streetNameTxt.setTypeface(mTypeface);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.streetNameTxt.setText(streets.get(position).getName());

        return convertView;
    }
}
