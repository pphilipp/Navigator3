package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 21:12
 * To change this template use File | Settings | File Templates.
 */
public class DispOrderResponse4 extends DispOrderResponse3 {
    public DispOrderResponse4(int id)
    {
        super(id);
    }

    public DispOrderResponse4(byte[] data)
    {
        super(ORDER_RESPONCE4);
        order = new DispOrder4();

        parse(data);
    }

    @Override
    public DispOrder4 getOrder() {
        return (DispOrder4)order;
    }

    @Override
    public void createOrder()
    {
        order = new DispOrder4();
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

        //sourceWhence
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        byte[] buffer = new byte[Utils.byteToInt(buffer4)];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder4)order).sourceWhence = StringUtils.bytesToStr(buffer);

        //OrderCostForDriver
        buffer = new byte[16];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder4)order).orderCostForDriver = Utils.byteToDecimal(buffer);

        //CanFirstForAnyParking
        ((DispOrder4)order).canFirstForAnyParking = (data[offset++] != 0);

        //DistanceToPointOfDelivery
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder4)order).distanceToPointOfDelivery = Utils.byteToDecimal(buffer);
        
        //Concessional
        ((DispOrder4)order).concessional = (data[offset++] != 0);
        
        //waitMinutes
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder4)order).waitMinutes = Utils.byteToDecimal(buffer);

        //waitMinutesPay
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        ((DispOrder4)order).waitMinutesPay = Utils.byteToDecimal(buffer);

        buffer4 = null;
        buffer = null;

        return super.parse(data, offset);
    }
}
