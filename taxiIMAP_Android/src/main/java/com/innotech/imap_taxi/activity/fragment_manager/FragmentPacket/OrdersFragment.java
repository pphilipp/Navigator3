package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

public class OrdersFragment extends FragmentPacket {
	private static final String LOG_TAG = OrdersFragment.class.getSimpleName();
	View myView;

	public OrdersFragment() {
		super(ORDERS);
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
	
	public static class ViewHolder {
		private Button curr;
		private Button done;
		private Button arch;
		private Button pre;
		private Button back;
		
		public ViewHolder(View view) {
			back = (Button) view.findViewById(R.id.btn_back);
			curr = (Button) view.findViewById(R.id.btn_curr);
			done = (Button) view.findViewById(R.id.btn_done_by_sess);
			arch = (Button) view.findViewById(R.id.btn_arch);
			pre = (Button) view.findViewById(R.id.btn_pre);
			
			curr.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d("myLogs", "check0");
					List<Order> ord = OrderManager.getInstance().getOrdersByState(
							Order.STATE_PERFORMING);
					for (Order i : ord) {
						Log.d("myLogs", "order index = " + ord.indexOf(i));
					}
					CurrentOrdersFragment
							.setState(CurrentOrdersFragment.STATE_PERFORMING);
					CurrentOrdersFragment
							.displayOrders(CurrentOrdersFragment.STATE_PERFORMING);

					FragmentTransactionManager.getInstance().openFragment(
							CURRENTORDERS);

				}
			});

			done.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					CurrentOrdersFragment
							.setState(CurrentOrdersFragment.STATE_PERFORMED);
					CurrentOrdersFragment
							.displayOrders(CurrentOrdersFragment.STATE_PERFORMED);
					FragmentTransactionManager.getInstance().openFragment(
							CURRENTORDERS);
				}
			});

			arch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ArchivOrdersFragment.displayOrders();
					FragmentTransactionManager.getInstance().openFragment(ARCHIV);
				}
			});

			pre.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					CurrentOrdersFragment
							.displayOrders(CurrentOrdersFragment.STATE_PRE);
					FragmentTransactionManager.getInstance().openFragment(
							CURRENTORDERS);
				}
			});
			
			back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					FragmentTransactionManager.getInstance().openFragment(
							FragmentPacket.SWIPE);
				}
			});
		}
	}

	private void populateViewForOrientation(LayoutInflater inflater,
			ViewGroup viewGroup) {
		viewGroup.removeAllViewsInLayout();
		View subview = inflater
				.inflate(R.layout.orders_fragment_new, viewGroup);
		
		ViewHolder viewHolder = new ViewHolder(subview);
		subview.setTag(viewHolder);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myView = inflater.inflate(R.layout.orders_fragment_new, container,
				false);

		ViewHolder viewHolder = (ViewHolder) myView.getTag();
		
		if (viewHolder == null)
			viewHolder = new ViewHolder(myView);
		
		return myView;
	}

	@Override
	public void onResume() {
		super.onResume(); // To change body of overridden methods use File |
							// Settings | File Templates.
		Log.d("myLogs", "OrdersFragment onResume");
		// balance.setText(UIData.getInstance().getBalance());
	}

}
