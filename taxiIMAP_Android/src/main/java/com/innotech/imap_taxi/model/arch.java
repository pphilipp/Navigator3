package com.innotech.imap_taxi.model;

public class arch {
	
	long date;
	String shortdesc, fulldesc, fulldescOther;
	
	public arch(long date, String shortdesc, String fulldesc, String fulldescOther) {
		super();
		this.date = date;
		this.shortdesc = shortdesc;
		this.fulldesc = fulldesc;
		if (fulldescOther == null) {
			this.fulldescOther = "";
		} else {
			this.fulldescOther = fulldescOther;
		}
	}

	public long getDate() {
		return date;
	}

	public String getShortdesc() {
		return shortdesc;
	}

	public String getFulldesc() {
		return fulldesc;
	}
	
	public String getFulldescOther() {
		return fulldescOther;
	}

}
