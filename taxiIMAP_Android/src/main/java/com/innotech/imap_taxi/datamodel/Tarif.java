package com.innotech.imap_taxi.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

public class Tarif {
	public String tariffname;
	public double pricetoseat,distanceinseat,priceperkilometr,priceforwait,pricedowntime,minspeedDowntime,freedowntime;
	public JSONObject jo;
	public Tarif(JSONObject job) {
		try {
			jo=job;
			tariffname=job.getString("tariffname");
			pricetoseat=job.getDouble("pricetoseat");
			distanceinseat=job.getDouble("distanceinseat");
			priceperkilometr=job.getDouble("priceperkilometr");
			priceforwait=job.getDouble("priceforwait");
			pricedowntime=job.getDouble("pricedowntime");
			minspeedDowntime=job.getDouble("minspeedDowntime");
			freedowntime=job.getDouble("freedowntown");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public Tarif(String j) throws JSONException {
		this(new JSONObject(j));
	}
	@Override
	public String toString() {
		return jo.toString();
	}
}
