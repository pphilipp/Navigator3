package com.innotech.imap_taxi.activity.fragment_manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.innotech.imap_taxi.activity.NavigatorMenuActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.ArchivOrdersFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.CurrentOrdersFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.EfirOrder;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.EthearFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.FragmentPacket;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.GetAddressFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.MapFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.OrderDetails;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.OrdersFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.OwnOrder;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.ParkingCardFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.ParkingsFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.SwipeFragment;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

import java.util.ArrayList;

/**
 *@class FragmentTransactionManager	- object that manage all of fragments.
 */
public class FragmentTransactionManager {
	public static final String LOG_TAG = FragmentTransactionManager.class.getSimpleName();
	private static FragmentTransactionManager instance;
	public FragmentTransaction fragmentTransaction;
	private FragmentActivity fragmentActivity;
	public ArrayList<FragmentPacket> stack;
	private int id;

	private FragmentTransactionManager() {
		stack = new ArrayList<FragmentPacket>();
		stack.add(new SwipeFragment());
//		 stack.add(new MenuFragmentPartTwo());
//		 stack.add(new EthearFragment());
		stack.add(new ParkingsFragment());
		stack.add(new EfirOrder());
		stack.add(new OrderDetails());
		stack.add(new MapFragment());
		stack.add(new OrdersFragment());
		stack.add(new CurrentOrdersFragment());
		stack.add(new ArchivOrdersFragment());
		stack.add(new OwnOrder());
		stack.add(new GetAddressFragment());
		stack.add(new ParkingCardFragment());
	}

	public static FragmentTransactionManager getInstance() {
		if (instance == null) {
			instance = new FragmentTransactionManager();
		}

		return instance;
	}

	public void initFragmentTransaction(FragmentActivity activity) {
		this.fragmentActivity = activity;
		fragmentTransaction = fragmentActivity.getSupportFragmentManager()
				.beginTransaction();
		for (Fragment f : stack) {
			fragmentTransaction.add(R.id.fragment_holder, f);
			fragmentTransaction.hide(f);
		}

		id = id != 0 ? id : stack.get(0).getIdFragment();
		for (FragmentPacket fr : stack) {
			if (fr.getIdFragment() == id) {
				fragmentTransaction.show(fr);
				if (NavigatorMenuActivity.iconLayout != null
				&& NavigatorMenuActivity.iconLayout != null) {
					if (id == 9) {
						NavigatorMenuActivity.arhivLayout
								.setVisibility(View.VISIBLE);
						NavigatorMenuActivity.iconLayout
								.setVisibility(View.GONE);
					} else {
						NavigatorMenuActivity.arhivLayout
								.setVisibility(View.GONE);
						NavigatorMenuActivity.iconLayout
								.setVisibility(View.VISIBLE);
					}
					// fr.onResume();
				}
			} else {
				fragmentTransaction.hide(fr);
			}
		}

		commit();
	}

	public void remove(FragmentActivity activity) {
		this.fragmentActivity = activity;
		fragmentTransaction = fragmentActivity.getSupportFragmentManager()
				.beginTransaction();
		for (Fragment f : stack) {
			fragmentTransaction.remove(f);
		}
		commit();
	}

	public void openFragment(final int id) {
		fragmentTransaction = fragmentActivity.getSupportFragmentManager()
				.beginTransaction();
		if (NavigatorMenuActivity.iconLayout != null
				&& NavigatorMenuActivity.iconLayout != null) {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				
				@Override
				public void run() {
					if (id == 9) {
						NavigatorMenuActivity.arhivLayout.setVisibility(View.VISIBLE);
						NavigatorMenuActivity.iconLayout.setVisibility(View.GONE);
					} else {
						NavigatorMenuActivity.arhivLayout.setVisibility(View.GONE);
						NavigatorMenuActivity.iconLayout.setVisibility(View.VISIBLE);
					}
					NavigatorMenuActivity.iconLayout
					.setVisibility((id == 4 || id == 9) ? View.GONE
							: View.VISIBLE);
				}
			});
		}

		for (FragmentPacket f : stack) {
			if (f.getIdFragment() == id) {
				if (this.id != id)
					f.setBackFragment(this.id);
				for (FragmentPacket fr : stack) {
					if (fr.getIdFragment() == id) {
						fragmentTransaction.show(fr);
						fr.onResume();
					} else
						fragmentTransaction.hide(fr);
				}
				this.id = id;
				break;
			}
		}
		commit();
	}

	public void back() {
		Log.d("parking_tag", "backPressed");
		fragmentTransaction = fragmentActivity.getSupportFragmentManager()
				.beginTransaction();
		boolean mBackFlag = false;
		for (FragmentPacket f : stack) {
			if (f.getIdFragment() == id) {
				id = f.getBackFragment();
				fragmentTransaction.hide(f);

				for (FragmentPacket fr : stack) {
					if (fr.getIdFragment() == id) {
						Log.d("myLogs", "archFrag control: id = " + id);
						NavigatorMenuActivity.iconLayout
								.setVisibility((id == 4 || id == 9) ? View.GONE
										: View.VISIBLE);
						NavigatorMenuActivity.arhivLayout
								.setVisibility(id == 9 ? View.VISIBLE
										: View.GONE);
						fragmentTransaction.show(fr);
						fr.onResume();
						mBackFlag = true;
						break;
					}
				}
				if (mBackFlag)
					break;
			}
		}
		commit();
	}

	private void commit() {
		switch (this.id) {
		case 0:
			NavigatorMenuActivity.activityTitle.setText("Главная");
			break;
		case 2:
			NavigatorMenuActivity.activityTitle.setText("Эфир");
			break;
		case 3:
			NavigatorMenuActivity.activityTitle.setText("Стоянки");
			break;
		case 4:
			NavigatorMenuActivity.activityTitle.setText("Заказ из эфира");
			break;
		case 5:
			NavigatorMenuActivity.activityTitle.setText("Заказ");
			break;
		case 6:
			NavigatorMenuActivity.activityTitle.setText("Карта");
			break;
		case 7:
			NavigatorMenuActivity.activityTitle.setText("Заказы");
			break;
		case 8:
			NavigatorMenuActivity.activityTitle.setText("Заказы");
			break;
		case 9:
			NavigatorMenuActivity.activityTitle.setText("Архив поездок");
			break;
		}
		try {
			fragmentTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return this.id;
	}

}
