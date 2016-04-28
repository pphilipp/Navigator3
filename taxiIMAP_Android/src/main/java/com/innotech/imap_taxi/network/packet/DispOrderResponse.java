package com.innotech.imap_taxi.network.packet;

import android.util.Log;

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
        Log.d("fill", "orderID " + order.orderID);

        //RelayID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.relayID = Utils.byteToInt(buffer4);
        Log.d("fill", "relayID " + order.relayID);

        //Folder
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.folder = StringUtils.bytesToStr(buffer);
        Log.d("fill", "folder " + order.folder);

        //nonCashPay
        order.nonCashPay = (data[offset++] != 0);
        Log.d("fill", "nonCashPay " + order.nonCashPay);

        //ClientTypes
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.clientType = StringUtils.bytesToStr(buffer);
        Log.d("fill", "clientType " + order.clientType);

        //Date
        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.date = Utils.byteToDate(buffer);
        Log.d("fill", "date " + order.date);

        //Fare
        buffer = new byte[16];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.fare = Utils.byteToDecimal(buffer);
        Log.d("fill", "fare " + order.fare);

        //DispatcherName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.dispatcherName = StringUtils.bytesToStr(buffer);
        Log.d("fill", "dispatcherName " + order.dispatcherName);

        //PhoneNumber
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.phoneNumber = StringUtils.bytesToStr(buffer);
        Log.d("fill", "phoneNumber " + order.phoneNumber);

        //StreetName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.streetName = StringUtils.bytesToStr(buffer);
        Log.d("fill", "streetName " + order.streetName);

        //House
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.house = StringUtils.bytesToStr(buffer);
        Log.d("fill", "house " + order.house);

        //AddressFact
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.addressFact = StringUtils.bytesToStr(buffer);
        Log.d("fill", "addressFact " + order.addressFact);


        //Flat
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.flat = StringUtils.bytesToStr(buffer);
        Log.d("fill", "flat " + order.flat);

        //Entrance
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.entrance = StringUtils.bytesToStr(buffer);
        Log.d("fill", "entrance " + order.entrance);


        //Building
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.building = StringUtils.bytesToStr(buffer);
        Log.d("fill", "building " + order.building);

        //Region
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.region = StringUtils.bytesToStr(buffer);
        Log.d("fill", "region " + order.region);

        //BonusSum
        buffer = new byte[16];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.bonusSum = Utils.byteToDecimal(buffer);
        Log.d("fill", "bonusSum " + order.bonusSum);

        //GeoX
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.geoX = Utils.byteToFloat(buffer4);
        Log.d("fill", "geoX " + order.geoX);

        //GeoY
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.geoY = Utils.byteToFloat(buffer4);
        Log.d("fill", "geoY " + order.geoY);

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
            Log.d("fill", "suborder.tariff " + suborder.tariff);

            //From
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            suborder.from = StringUtils.bytesToStr(buffer);
            Log.d("fill", "suborder.from " + suborder.from);

            //To
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            suborder.to = StringUtils.bytesToStr(buffer);
            Log.d("fill", "suborder.to " + suborder.to);

            //geoXFrom
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoXFrom = Utils.byteToFloat(buffer4);
            Log.d("fill", "suborder.geoXFrom " + suborder.geoXFrom);

            //geoYFrom
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoYFrom = Utils.byteToFloat(buffer4);
            Log.d("fill", "suborder.geoYFrom " + suborder.geoYFrom);

            //geoXTo
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoXTo = Utils.byteToFloat(buffer4);
            Log.d("fill", "suborder.geoXTo " + suborder.geoXTo);

            //geoYTo
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            suborder.geoYTo = Utils.byteToFloat(buffer4);
            Log.d("fill", "suborder.geoYTo " + suborder.geoYTo);

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
        Log.d("fill", "orderType " + order.orderType);

        //Comments
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.comments = StringUtils.bytesToStr(buffer);
        Log.d("fill", "comments " + order.comments);

        //isAdvanced
        order.isAdvanced = (data[offset++] != 0);
        Log.d("fill", "isAdvanced " + order.isAdvanced);

        //autoTariffClassUID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        order.autoTariffClassUID = Utils.byteToInt(buffer4);
        Log.d("fill", "autoTariffClassUID " + order.autoTariffClassUID);

        //PartnerPreffix
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.partnerPreffix = StringUtils.bytesToStr(buffer);
        Log.d("fill", "partnerPreffix " + order.partnerPreffix);

        //orderDispTime
        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        order.orderDispTime = Utils.byteToDate(buffer);
        Log.d("fill", "orderDispTime " + order.orderDispTime);

        buffer4 = null;
        buffer = null;

        Log.d("fill", "-------------------------------------------------------------");
        return offset;
    }
}
