package com.innotech.imap_taxi.network.packet;


/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 14.09.13
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public abstract class Packet {
    public static final int UNKNOWN_RESPONCE = -1;
    public static final int SERVER_ERROR_RESPONCE = 0;
    public static final int LOGIN_RESPONCE = 1;
    public static final int PING_RESPONCE = 2;
    public static final int SRV_MESSAGE_RESPONCE = 3;
    public static final int SRV_TRANSFER_DATA_RESPONCE = 4;
    public static final int REGISTER_ON_TAXI_PARKING_RESPONCE = 5;
    public static final int RELAY_COMMUNICATION_RESPONCE = 6;
    public static final int ORDER_RESPONCE = 7;
    public static final int TCPMESSAGE_RESPONCE = 8;
    public static final int REQUEST_CONFIRMATION_RESPONCE = 9;
    public static final int TAXI_PARKING_STATISTIC_RESPONCE = 10;
    public static final int TAXI_PARKING_LAST_CHANGE_DATE_RESPONCE = 11;
    public static final int WORK_REPORT_RESPONCE = 12;
    public static final int TAXI_PARKINGS_RESPONCE = 13;
    public static final int UNREGISTER_ON_TAXI_PARKING_RESPONCE = 14;
    public static final int DRIVER_PARKING_POSITION_RESPONCE = 15;
    public static final int PPCSETTINGS_RESPONCE = 16;
    public static final int CALL_SIGNCHANGED_RESPONCE = 17;
    public static final int GET_ORDERS_RESPONCE = 18;
    public static final int SQL_RESPONCE = 19;
    public static final int PREORDERS_RESPONCE = 20;
    public static final int CSBALANCE_RESPONCE = 21;
    public static final int DRIVER_MESSAGE_RESPONCE = 22;
    public static final int DRIVER_BALANCE_CHANGED_RESPONCE = 23;
    public static final int TAXI_PARKINGS_RESPONCE2 = 24;
    public static final int ORDER_RESPONCE2 = 25;
    public static final int ORDER_RESPONCE3 = 26;
    public static final int FILTERED_TAXI_PARKINGS_RESPONCE = 27;
    public static final int ORDER_RESPONCE4 = 28;
    public static final int PPSCHANGE_STATE_RESPONCE = 29;
    public static final int SETTINGS_XML_RESPONCE = 30;
    public static final int PING_STATE_ANSWER = 31;
    public static final int SET_YOUR_ORDERS_ANSWER = 32;
    public static final int ETHEAR_ORDER_OVER_ANSWER = 33;
    public static final int REFUSE_PRELIMINARY_ORDER_ANSWER = 34;
    public static final int GET_ROUTES_ANSWER = 35;
    public static final int SIGN_PRELIM_ORDER_ANSWER = 36;
    public static final int DRIVER_BLOCKED_PACK = 37;
    public static final int TAXIMETER_RATES = 38;
    public static final int ARCHIV_ORDERS_RESPONSE = 39;
    public static final int DISTANCE_ORDER_ANSWER_RESPONSE = 40;

    private int id;
    protected int mPacketNumber;

    public Packet(int id) {
        this.id = id;
        this.mPacketNumber = -1;
    }

    public int getId() {
        return id;
    }

    public int getPacketNumber() {
        return mPacketNumber;
    }

    public void setPacketNumber(int packetNumber) {
        mPacketNumber = packetNumber;
    }

    protected abstract void parse(byte[] data);
}
