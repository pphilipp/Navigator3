package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 17.11.13
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
public class SignPrelimOrdeResponce extends Packet {
    public int orderId;
    public List<String> callSigns;

    public SignPrelimOrdeResponce(byte[] data) {
        super(SIGN_PRELIM_ORDER_ANSWER);
        orderId = -1;
        callSigns = new ArrayList<String>();

        parse(data);
    }

    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //uint OrderId
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        orderId = Utils.byteToInt(buffer4);

        //List<string> CallSigns
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int count = Utils.byteToInt(buffer4);
        for (int i = 0; i < count; ++i) {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            int strSize = Utils.byteToInt(buffer4);
            byte[] buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            callSigns.add(StringUtils.bytesToStr(buffer));
        }

        buffer4 = null;
    }
}
