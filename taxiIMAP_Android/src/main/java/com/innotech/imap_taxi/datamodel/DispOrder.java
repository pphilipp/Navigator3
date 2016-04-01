package com.innotech.imap_taxi.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
public class DispOrder {
    public int orderID = -1;
    public int relayID = 0;
    public String folder = "";
    public boolean nonCashPay = false;
    public String clientType = "";
    public long date = 0;
    public float fare = -1;
    public String dispatcherName = "";
    public String phoneNumber = "";
    public String streetName = "";
    public String house = "";
    public String addressFact = "";
    public String flat = "";
    public String entrance = "";
    public String building = "";
    public String region = "";
    public float bonusSum = -1;
    public float geoX = 0;
    public float geoY = 0;
    public List<DispSubOrder> subOrders = new ArrayList<DispSubOrder>();
    public String orderType = "";
    public String comments = "";
    public boolean isAdvanced = false;
    public int autoTariffClassUID = 0;
    public String partnerPreffix = "";
    public long orderDispTime = 0;

    public static class DispSubOrder {
        public String tariff = "";
        public String from = "";
        public String to = "";
        public float geoXFrom = 0;
        public float geoYFrom = 0;
        public float geoXTo = 0;
        public float geoYTo = 0;

        public DispSubOrder() {}

        public DispSubOrder(String tariff, String from, String to,
                            float geoXFrom, float geoYFrom,
                            float geoXTo, float geoYTo) {
            this.tariff = tariff;
            this.from = from;
            this.to = to;
            this.geoXFrom = geoXFrom;
            this.geoYFrom = geoYFrom;
            this.geoXTo = geoXTo;
            this.geoYTo = geoYTo;
        }

        public JSONObject toArchive(){
            JSONObject archive = new JSONObject();
            try {
                archive.put("tariff",tariff);
                archive.put("from",from);
                archive.put("to",to);
                archive.put("geoXFrom",geoXFrom);
                archive.put("geoYFrom",geoYFrom);
                archive.put("geoXTo",geoXTo);
                archive.put("geoYTo",geoYTo);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return archive;
        }
        public DispSubOrder fromArchive(JSONObject archive){
            DispSubOrder item = new DispSubOrder();
            try {
                item.tariff = archive.getString("tariff");
                item.from = archive.getString("from");
                item.to = archive.getString("to");

                item.geoXFrom = Float.parseFloat(archive.getString("geoXFrom"));
                item.geoYFrom = Float.parseFloat(archive.getString("geoYFrom"));

                item.geoXTo = Float.parseFloat(archive.getString("geoXTo"));
                item.geoYTo = Float.parseFloat(archive.getString("geoYTo"));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return item;
        }
    }


}
