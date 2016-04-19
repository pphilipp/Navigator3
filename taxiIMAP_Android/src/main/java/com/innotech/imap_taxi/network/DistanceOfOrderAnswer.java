package com.innotech.imap_taxi.network;

import com.innotech.imap_taxi.network.packet.Packet;


public class DistanceOfOrderAnswer extends Packet {
    public int distance;
    public int orderID;

    public DistanceOfOrderAnswer(byte[] data) {
        super(DISTANCE_ORDER_ANSWER_RESPONSE);
        distance = 0;

        parse(data);
    }

    @Override
    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //orderID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        orderID = Utils.byteToInt(buffer4);

        //distance
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        distance = Utils.byteToInt(buffer4);

        buffer4 = null;
    }

    public int getDistance() {return distance;}
}
