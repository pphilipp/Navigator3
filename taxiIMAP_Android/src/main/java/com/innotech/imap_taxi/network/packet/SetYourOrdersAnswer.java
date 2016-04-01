package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.11.13
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class SetYourOrdersAnswer extends Packet {
    public String driverState;

    public SetYourOrdersAnswer(byte[] data) {
        super(SET_YOUR_ORDERS_ANSWER);
        driverState = "";

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //driverState
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        driverState = StringUtils.bytesToStr(buffer);
    }
}
