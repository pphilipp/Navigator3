package com.innotech.imap_taxi.datamodel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.innotech.imap_taxi.utile.XMLParser;

public class SettingsFromXml {
	
	private final String TAG = "IMAP";
	private static SettingsFromXml instance;
	
	private int minSendCoordsTime;
	private int maxSendCoordsTime;
	private int sendCoordsPerDistance;
	private int registerDriversTimeOut;
	private int firstTimeSearch;
	private int secondTimeSearch;
	private int thirdTimeSearch;
	private int AutoTariffClassUID;
	private float javaVersion;
	private String javaNewVersionLink;
	private int concurrentOrdersCount;
	private int serverWaitTime;
	private int clientWaitTime;
	private boolean autoCalculatedClientTime;
	private int auctionTime;
	private boolean checkDistToOrderAddr;
	private int minRadiusToOrderAddr;
	private boolean checkNoSubOrderForClose;
	private int maxDistToAddr;
	private int firstTimeSearchRadiusInMeters;
	private boolean allowDriverCancelOrder;
	private boolean sendNavigatorJavaChangeBalanceDriver;
	private boolean canUseTaxometer;
	private boolean useParkings;
	private boolean balanceDrivers;
	private boolean driverCanSendOrderCost;
	private String andriodVersion;
	private String andriodNewVersionLink;
	private boolean showYourOrders;
	private boolean notSendPrice;
	
	private int driverTime;
	
	private static final int DEFAULT_FIRST_TIME_SEARCH = 20000;
	private static final int DEFAULT_SECOND_TIME_SEARCH = 20000;
	private static final int DEFAULT_THIRD_TIME_SEARCH = 20000;
	private static final boolean DEFAULT_SHOW_YOUR_ORDERS = false;
	private static final boolean DEFAULT_USE_PARKINGS = false;

	public void setSettingsFromXml(String xml) {
		
		
		showYourOrders = DEFAULT_SHOW_YOUR_ORDERS;
		useParkings = DEFAULT_USE_PARKINGS;
		
		XMLParser parser = new XMLParser();
    	Document doc = parser.getDomElement(xml);
    	NodeList nl = doc.getElementsByTagName("PPCSettingsXML");
    	
    	for (int i = 0; i < nl.getLength(); i++) {
    		Element e = (Element) nl.item(i);
    		
    		Log.i(TAG, parser.getValue(e, "MinSendCoordsTime"));
    	    Log.i(TAG, parser.getValue(e, "MaxSendCoordsTime"));
    	    Log.i(TAG, parser.getValue(e, "SendCoordsPerDistance"));
    	    Log.i(TAG, parser.getValue(e, "RegisterDriversTimeOut"));
    	    Log.i(TAG, parser.getValue(e, "FirstTimeSearch"));
    	    Log.i(TAG, parser.getValue(e, "SecondTimeSearch"));
    	    Log.i(TAG, parser.getValue(e, "ThirdTimeSearch"));
    	    Log.i(TAG, parser.getValue(e, "AutoTariffClassUID"));
    	    Log.i(TAG, parser.getValue(e, "JavaVersion"));
    	    Log.i(TAG, parser.getValue(e, "JavaNewVersionLink"));
    	    Log.i(TAG, parser.getValue(e, "ConcurrentOrdersCount"));
    	    Log.i(TAG, parser.getValue(e, "ServerWaitTime"));
    	    Log.i(TAG, parser.getValue(e, "ClientWaitTime"));
    	    Log.i(TAG, parser.getValue(e, "AutoCalculatedClientTime"));
    	    Log.i(TAG, parser.getValue(e, "AuctionTime"));
    	    Log.i(TAG, parser.getValue(e, "CheckDistToOrderAddr"));
    	    Log.i(TAG, parser.getValue(e, "MinRadiusToOrderAddr"));
    	    Log.i(TAG, parser.getValue(e, "CheckNoSubOrderForClose"));
    	    Log.i(TAG, parser.getValue(e, "MaxDistToAddr"));
    	    Log.i(TAG, parser.getValue(e, "FirstTimeSearchRadiusInMeters"));
    	    Log.i(TAG, parser.getValue(e, "AllowDriverCancelOrder"));
    	    Log.i(TAG, parser.getValue(e, "SendNavigatorJavaChangeBalanceDriver"));
    	    Log.i(TAG, parser.getValue(e, "CanUseTaxometer"));
    	    Log.i(TAG, parser.getValue(e, "UseParkings"));
    	    Log.i(TAG, parser.getValue(e, "BalanceDrivers"));
    	    Log.i(TAG, "DriverCanSendOrderCost " + parser.getValue(e, "DriverCanSendOrderCost"));
    	    Log.i(TAG, parser.getValue(e, "AndriodVersion"));
    	    Log.i(TAG, parser.getValue(e, "AndriodNewVersionLink"));
    	    Log.i(TAG, "ShowYourOrders " + parser.getValue(e, "ShowYourOrders"));
    	    Log.i(TAG, "useParkings " + parser.getValue(e, "UseParkings"));
    	    Log.i(TAG, "NotSendPrice " + parser.getValue(e, "NotSendPrice"));
    	    Log.i(TAG, "DriverTime " + parser.getValue(e, "DriverTime"));
    		
//    		minSendCoordsTime = Integer.parseInt(parser.getValue(e, "MinSendCoordsTime"));
//    		maxSendCoordsTime = Integer.parseInt(parser.getValue(e, "MaxSendCoordsTime"));
//    		sendCoordsPerDistance = Integer.parseInt(parser.getValue(e, ""));
//    		registerDriversTimeOut = Integer.parseInt(parser.getValue(e, ""));
    		firstTimeSearch = Integer.parseInt(parser.getValue(e, "FirstTimeSearch"));
    		secondTimeSearch = Integer.parseInt(parser.getValue(e, "SecondTimeSearch"));
    		thirdTimeSearch = Integer.parseInt(parser.getValue(e, "ThirdTimeSearch"));
//    		AutoTariffClassUID = Integer.parseInt(parser.getValue(e, ""));
//    		javaVersion = Float.parseFloat(parser.getValue(e, ""));
//    		javaNewVersionLink =  parser.getValue(e, "");
//    		concurrentOrdersCount
//    		serverWaitTime
    		clientWaitTime = Integer.parseInt(parser.getValue(e, "ClientWaitTime"));
//    		autoCalculatedClientTime
//    		auctionTime
//    		checkDistToOrderAddr
//    		minRadiusToOrderAddr
//    		checkNoSubOrderForClose
//    		maxDistToAddr
//    		firstTimeSearchRadiusInMeters
    		allowDriverCancelOrder=Boolean.parseBoolean(parser.getValue(e, "AllowDriverCancelOrder"));
//    		sendNavigatorJavaChangeBalanceDriver
//    		canUseTaxometer
//    		useParkings
//    		balanceDrivers
    		driverCanSendOrderCost = Boolean.parseBoolean(parser.getValue(e, "DriverCanSendOrderCost"));
    		andriodVersion = parser.getValue(e, "AndriodVersion");
    		andriodNewVersionLink = parser.getValue(e, "AndriodNewVersionLink");
    		balanceDrivers = Boolean.parseBoolean(parser.getValue(e, "BalanceDrivers"));
    		showYourOrders = Boolean.parseBoolean(parser.getValue(e, "ShowYourOrders"));
    		useParkings = Boolean.parseBoolean(parser.getValue(e, "UseParkings"));
    		notSendPrice = Boolean.parseBoolean(parser.getValue(e, "NotSendPrice"));
    		driverTime = Integer.parseInt(parser.getValue(e, "DriverTime"));
    	    
    	}
    }

    public static SettingsFromXml getInstance() {

        if (instance == null) {
            instance = new SettingsFromXml();
        }

        return instance;
    }

	public int getMinSendCoordsTime() {
		return minSendCoordsTime;
	}

	public int getMaxSendCoordsTime() {
		return maxSendCoordsTime;
	}

	public int getSendCoordsPerDistance() {
		return sendCoordsPerDistance;
	}

	public int getRegisterDriversTimeOut() {
		return registerDriversTimeOut;
	}

	public int getFirstTimeSearch() {
		return (firstTimeSearch != 0) ? firstTimeSearch : DEFAULT_FIRST_TIME_SEARCH;
	}

	public int getSecondTimeSearch() {
		return (secondTimeSearch != 0) ? secondTimeSearch : DEFAULT_SECOND_TIME_SEARCH;
	}

	public int getThirdTimeSearch() {
		return (thirdTimeSearch != 0) ? thirdTimeSearch : DEFAULT_THIRD_TIME_SEARCH;
	}

	public int getAutoTariffClassUID() {
		return AutoTariffClassUID;
	}

	public float getJavaVersion() {
		return javaVersion;
	}

	public String getJavaNewVersionLink() {
		return javaNewVersionLink;
	}

	public int getConcurrentOrdersCount() {
		return concurrentOrdersCount;
	}

	public int getServerWaitTime() {
		return serverWaitTime;
	}

	public int getClientWaitTime() {
		return clientWaitTime;
	}

	public boolean isAutoCalculatedClientTime() {
		return autoCalculatedClientTime;
	}

	public int getAuctionTime() {
		return auctionTime;
	}

	public boolean isCheckDistToOrderAddr() {
		return checkDistToOrderAddr;
	}

	public int getMinRadiusToOrderAddr() {
		return minRadiusToOrderAddr;
	}

	public boolean isCheckNoSubOrderForClose() {
		return checkNoSubOrderForClose;
	}

	public int getMaxDistToAddr() {
		return maxDistToAddr;
	}

	public int getFirstTimeSearchRadiusInMeters() {
		return firstTimeSearchRadiusInMeters;
	}

	public boolean isAllowDriverCancelOrder() {
		return allowDriverCancelOrder;
	}

	public boolean isSendNavigatorJavaChangeBalanceDriver() {
		return sendNavigatorJavaChangeBalanceDriver;
	}

	public boolean isCanUseTaxometer() {
		return canUseTaxometer;
	}

	public boolean isUseParkings() {
		return useParkings;
	}

	public boolean isBalanceDrivers() {
		return balanceDrivers;
	}
	
	public boolean isDriverCanSendOrderCost() {
		return driverCanSendOrderCost;
	}
	
	public boolean isShowYourOrders() {
		return showYourOrders;
	}
	
	public boolean isNotSendPrice() {
		return notSendPrice;
	}
	
	public int getDriverTime() {
		return driverTime;
	}

}
