package com.innotech.imap_taxi.core;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Ura on 18.09.2014.
 */
public class StateObserver extends Observable {
	private boolean gps, network, server, showYourOrders, showParkings = false;
	public final static int DRIVER_FREE = 0, DRIVER_BUSY = 1,
			DRIVER_NO_CONNECT = 2, DRIVER_LOST_CONNECTION = 3;
	public final static boolean WORK = true, NO_WORK = false;
	private int driverState = 2, parkingPosition = 0;
	private String parkings = "Стоянки";
	static private StateObserver instance;

	public static StateObserver getInstance() {

		if (instance == null) {
			instance = new StateObserver();
		}
		return instance;
	}

	public boolean isGps() {
		return gps;
	}

	public void setGps(boolean gps) {
		this.gps = gps;
		setChanged();
		notifyObservers();
	}

	public boolean isNetwork() {
		return network;
	}

	public void setNetwork(boolean network) {
		this.network = network;
		setChanged();
		notifyObservers();
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean server) {
		this.server = server;
		setChanged();
		notifyObservers();
	}

	public int getDriverState() {
		return driverState;
	}

	public void setDriverState(int driverState) {
		this.driverState = driverState;
		setChanged();
		notifyObservers();
	}

	public String getParkings() {
		return parkings;
	}

	public void setParkings(String parkings) {
		this.parkings = parkings;
		setChanged();
		notifyObservers();
	}

	public boolean isShowYourOrders() {
		return showYourOrders;
	}

	public void setShowYourOrders(boolean showYourOrders) {
		this.showYourOrders = showYourOrders;
		setChanged();
		notifyObservers();
	}

	public boolean isShowParkings() {
		return showParkings;
	}

	public void setShowParkings(boolean showParkings) {
		this.showParkings = showParkings;
		setChanged();
		notifyObservers();
	}

	public int getParkingPosition() {
		return parkingPosition;
	}

	public void setParkingPosition(int parkingPosition) {
		this.parkingPosition = parkingPosition;
		setChanged();
		notifyObservers();
	}
}
