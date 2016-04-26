package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

public class OrdersFragment extends FragmentPacket implements View.OnClickListener{
	private static final String LOG_TAG = OrdersFragment.class.getSimpleName();

	public OrdersFragment() {
		super(ORDERS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.orders_fragment_new, container,false);
		ViewHolder viewHolder = (ViewHolder) v.getTag();

		if (viewHolder == null)
			viewHolder = new ViewHolder(v);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("myLogs", "OrdersFragment onResume");
		// balance.setText(UIData.getInstance().getBalance());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LayoutInflater inflater = LayoutInflater.from(ContextHelper
				.getInstance().getCurrentActivity());
		populateViewForOrientation(inflater, (ViewGroup) getView());
	}

	@Override
	public void onClick(View v) {

	}

	public static class ViewHolder implements View.OnClickListener{
		private Button btnCurrent;
//		private Button btnDone;
		private Button btnArch;
		private Button btnPre;
		private Button btnPack;

		public ViewHolder(View view) {
			btnPack = (Button) view.findViewById(R.id.btn_back);
			btnCurrent = (Button) view.findViewById(R.id.btn_curr);
//			btnDone = (Button) view.findViewById(R.id.btn_done_by_sess);
			btnArch = (Button) view.findViewById(R.id.btn_arch);
			btnPre = (Button) view.findViewById(R.id.btn_pre);

			btnPack.setOnClickListener(this);
			btnCurrent.setOnClickListener(this);
//			btnDone.setOnClickListener(this);
			btnArch.setOnClickListener(this);
			btnPre.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_back:
					FragmentTransactionManager.getInstance().openFragment(FragmentPacket.SWIPE);
					break;
				case R.id.btn_curr:
					Log.d("myLogs", "check0");
					List<Order> ord = OrderManager.getInstance()
							.getOrdersByState(Order.STATE_PERFORMING);
					for (Order i : ord)
						Log.d("myLogs", "order index = " + ord.indexOf(i));
					CurrentOrdersFragment.setState(CurrentOrdersFragment.STATE_PERFORMING);
					CurrentOrdersFragment.displayOrders(CurrentOrdersFragment.STATE_PERFORMING);
					FragmentTransactionManager.getInstance().openFragment(CURRENTORDERS);
					break;
//				case R.id.btn_done_by_sess:
//					CurrentOrdersFragment.setState(CurrentOrdersFragment.STATE_PERFORMED);
//					CurrentOrdersFragment.displayOrders(CurrentOrdersFragment.STATE_PERFORMED);
//					FragmentTransactionManager.getInstance().openFragment(CURRENTORDERS);
//					break;
				case R.id.btn_arch:
					ArchivOrdersFragment.displayOrders();
					FragmentTransactionManager.getInstance().openFragment(ARCHIV);
					break;
				case R.id.btn_pre:
					CurrentOrdersFragment.displayOrders(CurrentOrdersFragment.STATE_PRE);
					FragmentTransactionManager.getInstance().openFragment(CURRENTORDERS);
					break;
			}
		}
	}

	private void populateViewForOrientation(LayoutInflater inflater,ViewGroup viewGroup) {
		viewGroup.removeAllViewsInLayout();
		View subview = inflater.inflate(R.layout.orders_fragment_new, viewGroup);
		ViewHolder viewHolder = new ViewHolder(subview);
		subview.setTag(viewHolder);
	}

}
