package com.innotech.imap_taxi.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.graph_utils.GraphUtils;
import com.innotech.imap_taxi.graph_utils.RouteView;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.Utils;
import com.innotech.imap_taxi3.R;

public class OrdersAdapter extends BaseAdapter {
	protected List<Order> orders;
	protected Context mContext;
	protected LayoutInflater fInflater;
	protected Boolean longView = true;

	public void setLongView(Boolean longView) {
		this.longView = longView;
		this.notifyDataSetChanged();
	}

	public OrdersAdapter(List<Order> orders, Context mContext) {
		this.orders = orders;
		this.mContext = mContext;
		fInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	static class ViewHolderNewDesign {
//		public TextView date;
		public TextView time;
		public TextView orderFrom;
		public TextView orderTo;
		public TextView cost;
		public TextView distance;
		public ImageView imageFrom;
		public ImageView imageTo;
		public ImageView imageRoute;
		public ImageView imageJoker;
		public ImageView imageWebOrder;
		public ImageView imageNoCash;
		public ImageView imageEd;
		public LinearLayout colorLayout;
		public LinearLayout orderItem;
		public PaintDrawable p;
		public RelativeLayout routeLayout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// new design Sergey
		ViewHolderNewDesign myHolder;
		if (convertView == null) {
			if (longView) {
				/*convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_order_new1, null);*/
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_order_new2, null);
				Log.d("myLogs", "inadapter switch = long");
			} else {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_order_new_short, null);
				Log.d("myLogs", "inadapter switch = short");
			}
			myHolder = new ViewHolderNewDesign();

//			myHolder.date = (TextView) convertView.findViewById(R.id.distance_tv);
			myHolder.distance = (TextView) convertView.findViewById(R.id.distance_tv);

			myHolder.time = (TextView) convertView.findViewById(R.id.timeTxt);
			myHolder.orderFrom = (TextView) convertView
					.findViewById(R.id.fromTxt);
			//marquee
			/*myHolder.orderFrom.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			myHolder.orderFrom.setSingleLine(true);
			myHolder.orderFrom.setMarqueeRepeatLimit(5);
			myHolder.orderFrom.setSelected(true);*/

			if (longView) {
				myHolder.orderTo = (TextView) convertView
						.findViewById(R.id.toTxt);
				//marquee
				/*myHolder.orderTo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
				myHolder.orderTo.setSingleLine(true);
				myHolder.orderTo.setMarqueeRepeatLimit(5);
				myHolder.orderTo.setSelected(true);*/

				myHolder.imageFrom = (ImageView) convertView
						.findViewById(R.id.imageFrom);
				myHolder.imageTo = (ImageView) convertView
						.findViewById(R.id.imageTo);
				myHolder.imageRoute = (ImageView) convertView
						.findViewById(R.id.imageRoute);
				myHolder.imageJoker = (ImageView) convertView
						.findViewById(R.id.imageJoker);
				myHolder.imageWebOrder = (ImageView) convertView
						.findViewById(R.id.imageWeb);
				myHolder.imageNoCash = (ImageView) convertView
						.findViewById(R.id.imageNoCash);
				myHolder.imageEd = (ImageView) convertView
						.findViewById(R.id.imageEd);
			}

			myHolder.cost = (TextView) convertView.findViewById(R.id.costTxt);
			myHolder.colorLayout = (LinearLayout) convertView
					.findViewById(R.id.colorLayout);
			myHolder.orderItem = (LinearLayout) convertView
					.findViewById(R.id.order_item);

			convertView.setTag(myHolder);
		} else {
			myHolder = (ViewHolderNewDesign) convertView.getTag();
		}

		myHolder.p = (PaintDrawable) GraphUtils.getOrderItemGradient(convertView);
		convertView.setBackground((Drawable) myHolder.p);

		String date = (orders.get(position).getDate() == 0) ? "No date" : Utils
				.getDateString(orders.get(position).getDate());
		String time = (orders.get(position).getDate() == 0) ? "No time" : Utils
				.dateToTimeString(orders.get(position).getDate());
		float priceFloat = (orders.get(position).getFare() == 0) ? 0 : orders
				.get(position).getFare();
		String price = String.format("%.2f", priceFloat);
		String addressTo = "No addressTo";
//		String addressFrom = (orders.get(position).getAddress() != null) ? orders
//				.get(position).getAddressFact() : "No addressFrom";

		String street = (orders.get(position).getStreet() != null) ? orders
				.get(position).getStreet() : "No adress";
		String adressFact = (orders.get(position).getAddressFact() != null
				|| !orders.get(position).getAddressFact().equals("0") || !orders
				.get(position).getAddressFact().equals("")) ? orders.get(
				position).getAddressFact() : "";
		 String addressFrom = street + " " + adressFact;

		if (orders.get(position).getAddress().size() > 0) {
			addressTo = orders.get(position).getAddress()
					.get(orders.get(position).getAddress().size() - 1).to;
			// addressFrom = orders.get(position).getAddress().get(0).from;
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
						.getCurrentContext().getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");
		myHolder.orderFrom.setTypeface(t);
		myHolder.distance.setTypeface(t);

		int routeType = 0;

		if (longView) {
			myHolder.orderTo.setTypeface(t);
			myHolder.orderTo.setText(addressTo);

			// set images (longView)
			/*if (orders.get(position).getAddress().size() < 2) {
				myHolder.imageFrom.setImageResource(R.drawable.route_unknown);
			}
			if (orders.get(position).getAddress().size() == 1) {
				myHolder.imageFrom.setImageResource(R.drawable.route_2_points);
			}
			if (orders.get(position).getAddress().size() > 2) {
				myHolder.imageFrom.setImageResource(R.drawable.route_3_points);
			}*/

			//there is only start point

			if (orders.get(position).getAddress().size() < 1) {
				routeType = 1;
			}
			//there are start and end points
			if (orders.get(position).getAddress().size() == 1) {
				routeType = 2;
			}
			//there are a lot of points in route
			if (orders.get(position).getAddress().size() > 1) {
				routeType = 3;
			}



			// smth`s wrong with route points

			Boolean joker = (orders.get(position).agentName != null && !orders
					.get(position).agentName.equals(""));
			Boolean noCash = orders.get(position).isNonCashPay();
			Boolean webOrder = (!orders.get(position).getSourceWhence()
					.equals("Phone")
					&& !orders.get(position).getSourceWhence().equals("Skype") && !orders
					.get(position).getSourceWhence().equals("Mail"));
			Boolean edditional = (orders.get(position).getFeatures() != null && orders
					.get(position).getFeatures().size() > 0);

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
		}

		myHolder.cost.setTypeface(t);
		myHolder.distance.setText(Utils.intToSpannableStringKm(
				orders.get(position).getDistanceToOrderPlace()));
		myHolder.time.setText(time);
		myHolder.orderFrom.setText(addressFrom);
		myHolder.cost.setText(getPriceFormat(price));
		myHolder.colorLayout.setBackgroundColor(orders.get(position).colorClass);

		if (longView) {
			//test to draw route
			final RouteView rw = (RouteView) convertView
					.findViewById(R.id.routeCustom);
			rw.setRouteType(routeType);
			//end test
		}
		// end new design

		return convertView;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public int getCount() {
		return orders.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void updateMyList(List<Order> newlist) {
		orders = newlist;
		this.notifyDataSetChanged();
	}

	public SpannableString getPriceFormat(String price) {
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
