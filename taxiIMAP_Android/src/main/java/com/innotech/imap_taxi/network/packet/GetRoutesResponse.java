package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 17.11.13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class GetRoutesResponse extends Packet {
    public int orderId;
    public List<Float> geoX;
    public List<Float> geoY;

    public GetRoutesResponse(byte[] data) {
        super(GET_ROUTES_ANSWER);
        orderId = -1;
        geoX = new ArrayList<Float>();
        geoY = new ArrayList<Float>();

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

        //GeoX
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int count = Utils.byteToInt(buffer4);
        for (int i = 0; i < count; ++i) {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            geoX.add(Utils.byteToFloat(buffer4));
        }

        //GeoY
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        count = Utils.byteToInt(buffer4);
        for (int i = 0; i < count; ++i) {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            geoY.add(Utils.byteToFloat(buffer4));
        }

        buffer4 = null;
    }
}
