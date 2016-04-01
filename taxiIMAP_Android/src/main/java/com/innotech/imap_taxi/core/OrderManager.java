package com.innotech.imap_taxi.core;

import android.util.Log;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.CurrentOrdersFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.FragmentPacket;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.SwipeFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderManager {

	private static OrderManager instance;
	private List<Order> orders;
    public List<DispOrder4> etherOrders;
    public List<Order> archive;
	public static OrderManager getInstance() {

		if (instance == null) {
			instance = new OrderManager();
		}

		return instance;
	}


	public OrderManager() {
		super();
		orders = new ArrayList<Order>();
        etherOrders = new ArrayList<DispOrder4>();
        archive = new ArrayList<Order>();
	}

	public void addOrder(Order newOrd) {

		Log.i("orderManager", "add order");
		Log.w("orderManager", newOrd.getOrderType());

		boolean found = false;
		int count = 0;
		int index = -1;
		for (Order item : orders) {
			if (item.getOrderID() == newOrd.getOrderID()) {
				//item = newOrd;
				Log.e("IMAP", Utils.dateToTimeString(item.getDate()) + " -> " + Utils.dateToTimeString(newOrd.getDate()));
				newOrd.setDate3(item.getDate3());
				newOrd.arrived = item.arrived;
                newOrd.accepted = item.accepted;
				//orders.remove(item);
				//orders.add(newOrd);
                index = count;
				Log.d("Order Manager",newOrd.getOrderID() + " found in added " + newOrd.getStatus());
				Log.i("orderManager", "edited order ( performing - " + OrderManager.getInstance().getCountOfOrdersByState(Order.STATE_PERFORMING));

				found = true;
				break;
			}
			count++;
		} 
		if (index > -1) {
			orders.remove(index);
			orders.add(newOrd);
		}

		if (!found) {
			Log.e("IMAP", Utils.dateToTimeString(newOrd.getDate()));
			orders.add(newOrd); 
			Log.d("OrderManager",newOrd.getOrderID() + " added " + newOrd.getStatus());
            Log.i("orderManager", "added order ( performing - " + OrderManager.getInstance().getCountOfOrdersByState(Order.STATE_PERFORMING));
		}

		ContextHelper.getInstance().runOnCurrentUIThread(
				new Runnable() {
					@Override
					public void run() {
						SwipeFragment.ethear.setText("ЭФИР(" + getCountOfEfirOrders() + ")");
					}
				});
	}



	public List<Order> getEfirOrders()
	{
		List<Order> result = new ArrayList<Order>();

		for(Order od : orders)
		{
			if (od.getStatus() == Order.STATE_NEW)
			{
				result.add(od);
			}
		}

		return result;
	}

	public List<Order> getOrders(){
		return orders;
	}

	public int getCountOfEfirOrders()
	{
		int count = 0;

		if (orders != null && orders.size() > 0) {
			/*for (Order od : orders) {
				if (od.getStatus() == Order.STATE_NEW) {
					count++;
				}
			}*/
			for (int i = 0; i < orders.size(); i++)
				if (orders.get(i).getStatus() == Order.STATE_NEW)
					count++;
		}
		return count;
	}

	public int getCountOfOrdersByState(int state)
	{
		int count = 0;

		/*for(Order od : orders)
		{
			if (od.getStatus() == state)
			{
				count++;
			}
		}*/
		for (int i = 0; i < orders.size(); i++)
			if (orders.get(i).getStatus() == state)
				count++;

		return count;
	}

	public List<Order> getOrdersByState(int state) {
		List<Order> result = new ArrayList<Order>();

		for(Order od : orders)
		{
			if (od.getStatus() == state)
			{
				result.add(od);
			}
		}

		return result;
	}


	public void removeOrder(Order order) {

		Log.i("orderManager", "removeOrder");
		
		int count = 0;
		int index = -1;
		for(Order od : orders)
		{
			if (od.getOrderID() == order.getOrderID())
			{
				//System.out.println("FOUND THIS MOTHER FUCKER");
				Log.d("OrderManager",order.getOrderID() + " removed " + order.getStatus());
//				LogHelper.w(order.getOrderID() + " removed " + order.getStatus());
				index = count;
				//orders.remove(od);
				break;
			}
			count++;
		}
		if (index > -1)
			orders.remove(index);
		//countChangedEvent();
	}

	public Order getOrder(int orderId)
	{

		//Log.i("orderManager", "getOrder");

		for(Order od : orders)
		{
			if (od.getOrderID() == orderId)
			{
				return od;
			}
		}

		return null;
	}

	public void changeOrderState(int orderId, int state) {

		Log.i("orderManager", "changeOrderState" + orderId + " " + state);
		for(Order od : orders) {
			if (od != null && od.getOrderID() == orderId /*&& !(od.getStatus()==Order.STATE_PERFORMING && state!=Order.STATE_MISSED)*/) {
				Log.d("OrderManager",orderId + " changeOrderState " + od.getStatus() + " to " + state);
//				LogHelper.w(orderId + " changeOrderState " + od.getStatus() + " to " + state);
                Log.w("orderManager", "changeOrderState " + od.getStatus() + " to " + state);
                od.setStatus(state);
                
				break;
			}
		}
		if (FragmentTransactionManager.getInstance().getId() == FragmentPacket.CURRENTORDERS) {
			CurrentOrdersFragment.displayOrders(Order.STATE_PERFORMING);
			Log.d("STATE", "need to change list of current orders");
			}
	}


	public void setDateTime(int orderId, int parseInt) {

		Log.i("orderManager", "setDateTime " + orderId + " " + parseInt);

		for(Order od : orders)
		{
			if (od.getOrderID() == orderId)
			{
				long dateTime3 = new Date().getTime() + (parseInt * 60000);
				Log.i("orderManager", "setDateTime " + " " + dateTime3);
				od.setDate3(dateTime3);
				break;
			}
		}
	}

	public void setDateNoClient(int orderId, long date) {

		Log.i("orderManager", "setDateNoClient " + orderId);

		for(Order od : orders)
		{
			if (od.getOrderID() == orderId)
			{
				//long dateTime = new Date().getTime();
				Log.i("orderManager", "setDateNoClient " + date);
				od.dateNoClient = date;
				break;
			}
		}
	}

	public void setOrderFromServerFalse(int orderId) {
		for(Order od : orders)
		{
			if (od.getOrderID() == orderId)
			{
				od.setFromServer(false);
				break;
			}
		}
	}


	public void signToPreOrder(int orderId) {

		for(Order od : orders)
		{
			if (od.getOrderID() == orderId)
			{
				od.setSigned(true);
				break;
			}
		}
	}


	public void clearAllOrders() {
		orders = new ArrayList<Order>();
	}


	public void changeOrderFolder(int orderID, String fold) {
		for(Order od : orders)
		{
			if (od.getOrderID() == orderID)
			{
				od.setFolder(fold);
				break;
			}
		}
	}


	public void setPressedArrived(int orderID) {
		for(Order od : orders)
		{
			if (od.getOrderID() == orderID)
			{
				System.out.println("setPressedArrived true");
				od.arrived = true;
				break;
			}
		}
	}

}
