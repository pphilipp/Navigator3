package com.innotech.imap_taxi.helpers;

import android.util.Log;

import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.model.UIData;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.RequestBuilder;

public class RequestHelper {
	public static final String LOG_TAG = RequestHelper.class.getSimpleName();
	
	public static void getRoutes(int orderId) {
		byte[] body = RequestBuilder.createBodyGetRoutes(orderId, ServerData.getInstance().getPeopleID());
        byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
                RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body);
        ConnectionHelper.getInstance().send(data);
	}
	
	public static void requestAir() {
		byte[] body = RequestBuilder.createRequestAir(ServerData.getInstance().getPeopleID());
        byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
                RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body);
        ConnectionHelper.getInstance().send(data);
	}
	
	public static void getOrders() {
		//String filter = "AutoDriverID = "+ ServerData.getInstance().getPeopleID() +" AND Folder <> 23 AND Folder <> 24 AND Folder <> 25 AND Folder <> 26";
		String filter = "AutoDriverID = " + ServerData.getInstance().getPeopleID()
				+ " AND Folder <> 24 AND Folder <> 25 AND Folder <> 26";
	    byte[] body = RequestBuilder.createBodyGetOrders(filter);
	    byte[] data = RequestBuilder.createSrvTransfereData(
				RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
	            RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(),
				true, body);
	    ConnectionHelper.getInstance().send(data);
		Log.d(LOG_TAG,data.toString());
	}
	
	public static void versionDeviceSoftware() {
		byte[] body4 = RequestBuilder.createVersionDeviceSoftware("3", UIData.getInstance().getVersion(), ServerData.getInstance().getPeopleID());
        byte[] data4 = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
                RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body4);
        ConnectionHelper.getInstance().send(data4);
	}
	
	public static void connectDriverClient(int orderId) {
		byte[] body = RequestBuilder.createConnectDriverClient(orderId, ServerData.getInstance().getPeopleID());
		byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
	            RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body);
	    ConnectionHelper.getInstance().send(data);
	}
	
	public static void dangerWarning() {
		byte[] body = RequestBuilder.createDangerWarning(0, 0, ServerData.getInstance().getPeopleID());
		byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
	            RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body);
	    ConnectionHelper.getInstance().send(data);
	}
	
	public static void getTaxiParkings() {
		byte[] body4 = RequestBuilder.createBodyGetTaxiParkings(ServerData.getInstance().getPeopleID());
        byte[] data4 = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
                RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true, body4);
        ConnectionHelper.getInstance().send(data4);
	}
	
}
