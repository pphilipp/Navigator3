package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.datamodel.DispOrder3;
import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 21:11
 * To change this template use File | Settings | File Templates.
 */
public class DispOrderResponse3 extends DispOrderResponse2 {
    public DispOrderResponse3(int id)
    {
        super(id);
    }

    public DispOrderResponse3(byte[] data)
    {
        super(ORDER_RESPONCE3);
        order = new DispOrder3();

        parse(data);
    }

    @Override
    public DispOrder3 getOrder() {
        return (DispOrder3)order;
    }

    @Override
    public void createOrder()
    {
        order = new DispOrder3();
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

        //partnerID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        ((DispOrder3)order).partnerID = Utils.byteToInt(buffer4);

        //partnerName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder3)order).partnerName = StringUtils.bytesToStr(buffer);

        //orderShortDesc
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder3)order).orderShortDesc = StringUtils.bytesToStr(buffer);

        //orderFullDesc
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder3)order).orderFullDesc = StringUtils.bytesToStr(buffer);

        //orderPrelimFullDesc
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder3)order).orderPrelimFullDesc = StringUtils.bytesToStr(buffer);

        buffer4 = null;
        buffer = null;

        return super.parse(data, offset);
    }
}
