package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.datamodel.DispOrder;
import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class DispOrderResponse extends Packet {
    protected DispOrder order;

    public DispOrderResponse(int id)
    {
        super(id);
    }

    public DispOrderResponse(byte[] data) {
        super(ORDER_RESPONCE);
        order = new DispOrder();

        parse(data);
    }

    public DispOrder getOrder() {return order;}

    public void createOrder()
    {
        order = new DispOrder();
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

    public int parse(byte[] data, int offset) {
        byte[] buffer4 = new byte[4];

        //OrderID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.orderID = Utils.byteToInt(buffer4);

        //RelayID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.relayID = Utils.byteToInt(buffer4);

        //Folder
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.folder = StringUtils.bytesToStr(buffer);

        //nonCashPay
        order.nonCashPay = (data[offset++] != 0);

        //ClientTypes
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.clientType = StringUtils.bytesToStr(buffer);

        //Date
        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.date = Utils.byteToDate(buffer);

        //Fare
        buffer = new byte[16];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.fare = Utils.byteToDecimal(buffer);

        //DispatcherName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.dispatcherName = StringUtils.bytesToStr(buffer);

        //PhoneNumber
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.phoneNumber = StringUtils.bytesToStr(buffer);

        //StreetName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.streetName = StringUtils.bytesToStr(buffer);

        //House
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.house = StringUtils.bytesToStr(buffer);

        //AddressFact
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.addressFact = StringUtils.bytesToStr(buffer);

        //Flat
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.flat = StringUtils.bytesToStr(buffer);

        //Entrance
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.entrance = StringUtils.bytesToStr(buffer);

        //Building
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.building = StringUtils.bytesToStr(buffer);

        //Region
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.region = StringUtils.bytesToStr(buffer);

        //BonusSum
        buffer = new byte[16];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.bonusSum = Utils.byteToDecimal(buffer);

        //GeoX
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.geoX = Utils.byteToFloat(buffer4);

        //GeoY
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.geoY = Utils.byteToFloat(buffer4);

        //SubOrders
        //SubOrders count
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int count = Utils.byteToInt(buffer4);

        for (int j=0;j<count;j++) {
            DispOrder.DispSubOrder suborder = new DispOrder.DispSubOrder();

            offset += 4;

            //Tariff
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            suborder.tariff = StringUtils.bytesToStr(buffer);

            //From
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            suborder.from = StringUtils.bytesToStr(buffer);

            //To
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            suborder.to = StringUtils.bytesToStr(buffer);

            //geoXFrom
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoXFrom = Utils.byteToFloat(buffer4);

            //geoYFrom
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoYFrom = Utils.byteToFloat(buffer4);

            //geoXTo
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoXTo = Utils.byteToFloat(buffer4);

            //geoYTo
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoYTo = Utils.byteToFloat(buffer4);

            order.subOrders.add(suborder);
        }

        //OrderType
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.orderType = StringUtils.bytesToStr(buffer);

        //Comments
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.comments = StringUtils.bytesToStr(buffer);

        //isAdvanced
        order.isAdvanced = (data[offset++] != 0);

        //autoTariffClassUID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.autoTariffClassUID = Utils.byteToInt(buffer4);

        //PartnerPreffix
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.partnerPreffix = StringUtils.bytesToStr(buffer);

        //orderDispTime
        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.orderDispTime = Utils.byteToDate(buffer);

        buffer4 = null;
        buffer = null;

        return offset;
    }
}
