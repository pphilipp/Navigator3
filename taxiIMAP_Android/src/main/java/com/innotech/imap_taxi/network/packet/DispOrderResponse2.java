package com.innotech.imap_taxi.network.packet;

import java.io.UnsupportedEncodingException;

import android.graphics.Color;
import android.util.Log;

import com.innotech.imap_taxi.datamodel.DispOrder2;
import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 20:57
 * To change this template use File | Settings | File Templates.
 */
public class DispOrderResponse2 extends DispOrderResponse {
    public DispOrderResponse2(int id)
    {
        super(id);
    }

    public DispOrderResponse2(byte[] data)
    {
        super(ORDER_RESPONCE2);
        order = new DispOrder2();

        parse(data);
    }

    @Override
    public DispOrder2 getOrder() {
        return (DispOrder2)order;
    }

    @Override
    public void createOrder()
    {
        order = new DispOrder2();
    }

    @Override
    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        parse(data, offset);
    }

    @Override
    public int parse(byte[] data, int offset)
    {
        byte[] buffer4 = new byte[4];

        //agentPay
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder2)order).agentPay = StringUtils.bytesToStr(buffer);

        //features
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int count = Math.max(Utils.byteToInt(buffer4), 0);

        for (int j = 0; j < count; ++j)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            ((DispOrder2)order).features.add(StringUtils.bytesToStr(buffer));
        }

        //agentName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder2)order).agentName = StringUtils.bytesToStr(buffer);

        //dispPhone
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder2)order).dispPhone = StringUtils.bytesToStr(buffer);

        //ColorClass
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        ((DispOrder2)order).colorClass = Utils.byteToInt(buffer4);

        //autoClass;
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder2)order).autoClass = StringUtils.bytesToStr(buffer);

        //searchType
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        ((DispOrder2)order).searchType = Utils.byteToInt(buffer4);

        //clientName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder2)order).clientName = StringUtils.bytesToStr(buffer);

        //receiptMustHave;
        ((DispOrder2)order).receiptMustHave = (data[offset++] != 0);

       /* System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder2)order).preliminary = Boolean.parseBoolean(StringUtils.bytesToStr(buffer));
        */
        
        buffer4 = null;
        buffer = null;

        return super.parse(data, offset);
    }
}