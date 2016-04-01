package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 09.10.13
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
public class PPSChangeStateResponse extends Packet {
    public String state;
    public int orderID;

    public PPSChangeStateResponse(byte[] data) {
        super(PPSCHANGE_STATE_RESPONCE);

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //state
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        state = StringUtils.bytesToStr(buffer);

        //orderID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        orderID = Utils.byteToInt(buffer4);

        buffer4 = null;
        buffer = null;
    }
}
