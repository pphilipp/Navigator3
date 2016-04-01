package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.Utils;

public class SearchInEtherOrderOverResponse extends Packet {

    public int orderID;

    public SearchInEtherOrderOverResponse(byte[] data) {
        super(ETHEAR_ORDER_OVER_ANSWER);
        orderID = -1;

        parse(data);
    }

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

        buffer4 = null;
    }
}
