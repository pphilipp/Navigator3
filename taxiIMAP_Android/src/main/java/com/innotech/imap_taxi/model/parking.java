package com.innotech.imap_taxi.model;


import java.util.Comparator;

public class parking implements Comparable<parking>{
	
	private int uid, count;
	private String parkingName, parkingRegion;
	private long responseDate;
	String driverNames;
	public String getDriverNames() {
		return (driverNames==null)?"":driverNames;
	}

	public void setDriverNames(String driverNames) {
		if (driverNames==null)
			driverNames="";
		this.driverNames = driverNames;
	}
	
	public parking(int uid, String parkingName, String parkingRegion,
			long responseDate) {
		super();
		this.uid = uid;
		this.parkingName = parkingName;
		this.parkingRegion = parkingRegion;
		this.responseDate = responseDate;
	}
	
	public int getUid() {
		return uid;
	}
	public String getParkingName() {
		return parkingName;
	}
	public String getParkingRegion() {
		return parkingRegion;
	}
	public long getResponseDate() {
		return responseDate;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount() {
		return count;
	}


	@Override
	public int compareTo(parking another) {
		//order list by parkings names AZ
		String amount = another.getParkingName();
		return this.getParkingName().toUpperCase().compareTo(amount.toUpperCase());
	}

	//sort by names ZA
	public static Comparator<parking> orderParkingsByNameZA = new Comparator<parking>() {
		@Override
		public int compare(parking lhs, parking rhs) {
			return rhs.getParkingName().toUpperCase().compareTo(lhs.getParkingName().toUpperCase());
		}
	};

	//sort by queue
	public static Comparator<parking> orderParkingsByQueue = new Comparator<parking>() {
		@Override
		public int compare(parking lhs, parking rhs) {
			return (int) (lhs.getCount() - rhs.getCount());
		}
	};

}
