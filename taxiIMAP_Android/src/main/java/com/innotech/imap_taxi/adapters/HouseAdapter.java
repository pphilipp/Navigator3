package com.innotech.imap_taxi.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innotech.imap_taxi.datamodel.house;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

import java.util.List;

/**
 * Created by Sergey on 16.11.2015.
 */
public class HouseAdapter extends BaseAdapter {

    private final Typeface mTypeface;
    private Context mContext;
    private List<house> houses;

    public HouseAdapter(Context mContext, List<house> houses) {
        this.mContext = mContext;
        this.houses = houses;
        mTypeface = Typeface.createFromAsset(ContextHelper.getInstance()
                .getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");
    }

    private class ViewHolder{

        TextView houseNameTxt;

    }

    public void upDateList(List<house> houses) {
        this.houses = houses;
        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return houses.size();
    }

    @Override
    public house getItem(int position) {
        return houses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) ContextHelper.getInstance().getCurrentContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.house_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.houseNameTxt = (TextView) convertView.findViewById(R.id.houseName_txt);
            viewHolder.houseNameTxt.setTypeface(mTypeface);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.houseNameTxt.setText(houses.get(position).getValue());

        return convertView;
    }
}
