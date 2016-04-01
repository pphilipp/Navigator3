package com.innotech.imap_taxi.adapters;

import java.util.List;

import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.graph_utils.GraphUtils;
import com.innotech.imap_taxi.graph_utils.RouteView;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.Utils;
import com.innotech.imap_taxi3.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrdersAdapterDisp4 extends BaseAdapter {

	protected List<DispOrder4> orders;
	protected Context mContext;
	protected LayoutInflater fInflater;

	public OrdersAdapterDisp4(List<DispOrder4> orders, Context mContext) {
		this.orders = orders;
		this.mContext = mContext;
		fInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	static class ViewHolderNewDesign {
		public TextView date, time, orderFrom, orderTo, cost;
		public ImageView imageFrom, imageTo, imageJoker, imageWebOrder,
				imageNoCash, imageEd;
		public LinearLayout colorLayout;
		public PaintDrawable p;
	}

	@Override
	public int getCount() {
		return orders.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void updateMyList(List<DispOrder4> newlist) {
		orders = newlist;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// new design Sergey
		ViewHolderNewDesign myHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_order_new2, null);

			myHolder = new ViewHolderNewDesign();
			myHolder.date = (TextView) convertView.findViewById(R.id.distance_tv);
			myHolder.time = (TextView) convertView.findViewById(R.id.timeTxt);
			myHolder.orderFrom = (TextView) convertView
					.findViewById(R.id.fromTxt);
			myHolder.orderTo = (TextView) convertView.findViewById(R.id.toTxt);
			myHolder.cost = (TextView) convertView.findViewById(R.id.costTxt);
			myHolder.imageFrom = (ImageView) convertView
					.findViewById(R.id.imageFrom);
			myHolder.imageTo = (ImageView) convertView
					.findViewById(R.id.imageTo);
			myHolder.imageJoker = (ImageView) convertView
					.findViewById(R.id.imageJoker);
			myHolder.imageWebOrder = (ImageView) convertView
					.findViewById(R.id.imageWeb);
			myHolder.imageNoCash = (ImageView) convertView
					.findViewById(R.id.imageNoCash);
			myHolder.imageEd = (ImageView) convertView
					.findViewById(R.id.imageEd);
			myHolder.colorLayout = (LinearLayout) convertView
					.findViewById(R.id.colorLayout);
			convertView.setTag(myHolder);
		} else {
			myHolder = (ViewHolderNewDesign) convertView.getTag();
		}

		myHolder.p = (PaintDrawable) GraphUtils
				.getOrderItemGradient(convertView);
		convertView.setBackground((Drawable) myHolder.p);
		
		
		//TODO get and set here distance in km
		String routeDistance = "22.5"
				+ ContextHelper.getInstance().getCurrentContext()
						.getResources().getString(R.string.km);
		SpannableStringBuilder ss = new SpannableStringBuilder(routeDistance);
		int color = Color.parseColor("#979695");
		ForegroundColorSpan fs = new ForegroundColorSpan(color);

		ss.setSpan(new RelativeSizeSpan(0.85f), routeDistance.length() - 2,
				routeDistance.length(), 0);
		ss.setSpan(fs, routeDistance.length() - 2, routeDistance.length(), 0);
		myHolder.date.setText(ss);
		String time = (orders.get(position).date == 0) ? "No time" : Utils
				.dateToTimeString(orders.get(position).date);
		/*
		 * if (orders.get(position).preliminary) {
		 * myHolder.time.setVisibility(View.VISIBLE); } else {
		 * myHolder.time.setVisibility(View.INVISIBLE); }
		 */
		float priceFloat = (orders.get(position).fare == 0) ? 0 : orders
				.get(position).fare;
		if (priceFloat == 0) {
			myHolder.cost.setVisibility(View.INVISIBLE);
		} else {
			myHolder.cost.setVisibility(View.VISIBLE);
		}
		String price = String.format("%.2f", priceFloat);

		String addressFrom = (orders.get(position).addressFact != null) ? orders
				.get(position).addressFact : "No addressFrom";

		String street = (orders.get(position).streetName != null) ? orders
				.get(position).streetName : "No adress";
		String adressFact = (orders.get(position).addressFact != null
				|| !orders.get(position).addressFact.equals("0") || !orders
				.get(position).addressFact.equals("")) ? orders.get(position).addressFact
				: "";

		addressFrom = street + " " + adressFact;
		String addressTo = "";
		int subOrdersLength = 0;

		if (orders.get(position).subOrders.size() > 0) {
			subOrdersLength = orders.get(position).subOrders.size();
			addressTo = (orders.get(position).subOrders
					.get(subOrdersLength - 1).to != null) ? orders
					.get(position).subOrders.get(subOrdersLength - 1).to : "";
		}

		// set all fields
		// set fonts
		// font for time
		Typeface t = Typeface
				.createFromAsset(ContextHelper.getInstance()
						.getCurrentContext().getAssets(),
						"fonts/TickingTimebombBB.ttf");
		myHolder.time.setTypeface(t);
		// set font for all textViews
		t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");
		myHolder.orderFrom.setTypeface(t);
		// myHolder.date.setTypeface(t);
		myHolder.orderTo.setTypeface(t);
		myHolder.cost.setTypeface(t);

		myHolder.time.setText(time);
		myHolder.orderFrom.setText(addressFrom);
		myHolder.orderTo.setText(addressTo);
		myHolder.cost.setText(getPriceFormat(price));
		t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/Exo2-Medium.otf");
		myHolder.date.setTypeface(t);
		myHolder.date.setTextSize(18);

		// set images
		/*if (subOrdersLength < 2) {
			myHolder.imageFrom.setImageResource(R.drawable.route_unknown);
		}
		if (subOrdersLength == 1 && !addressTo.equals("No addressFrom")) {
			myHolder.imageFrom.setImageResource(R.drawable.route_2_points);
		}
		if (subOrdersLength > 1) {
			myHolder.imageFrom.setImageResource(R.drawable.route_3_points);
		}*/
		
		int routeType = 0;
		if (subOrdersLength < 1) {
			routeType = 1;
		}
		//there are start and end points
		if (subOrdersLength == 1) {
			routeType = 2;
		}
		//there are a lot of points in route
		if (subOrdersLength > 1) {
			routeType = 3;
		}
		
		// smth`s wrong with route points

		// temp data for images
		Boolean joker = (orders.get(position).agentName != null) ? true : false;
		Boolean noCash = orders.get(position).nonCashPay;
		Boolean webOrder = (!orders.get(position).sourceWhence.equals("Phone")
				&& !orders.get(position).sourceWhence.equals("Skype") && !orders
				.get(position).sourceWhence.equals("Mail")) ? true : false;
		Boolean edditional = (orders.get(position).features.size() > 0) ? true
				: false;

		// set extra info images
		if (joker)
			myHolder.imageJoker.setVisibility(View.VISIBLE);
		else
			myHolder.imageJoker.setVisibility(View.GONE);
		if (noCash)
			myHolder.imageNoCash.setVisibility(View.VISIBLE);
		else
			myHolder.imageNoCash.setVisibility(View.GONE);
		if (webOrder)
			myHolder.imageWebOrder.setVisibility(View.VISIBLE);
		else
			myHolder.imageWebOrder.setVisibility(View.GONE);
		if (edditional)
			myHolder.imageEd.setVisibility(View.VISIBLE);
		else
			myHolder.imageEd.setVisibility(View.GONE);

		// get colors for order class
		myHolder.colorLayout
				.setBackgroundColor(orders.get(position).colorClass);
		
		//test to draw route
		final RouteView rw = (RouteView) convertView
				.findViewById(R.id.routeCustom);
		rw.setRouteType(routeType);
		//end test

		return convertView;
	}

	private SpannableString getPriceFormat(String price) {
		SpannableString ss = null;
		String[] parts = null;
		int length;
		if (price.contains(".")) {
			price = price.replace(".", ",");
		}
		if (price.contains(",")) {
			parts = price.split(",");
			if (parts[0] == null)
				parts[0] = "00";
			if (parts[1] == null)
				parts[1] = "00";
			length = parts[0].length();
			price = parts[0] + "," + parts[1];
		} else {
			length = price.length();
			price = price + ",00";
		}
		ss = new SpannableString(price);
		ss.setSpan(new RelativeSizeSpan(0.6f), length + 1, price.length(), 0);
		return ss;
	}

}
