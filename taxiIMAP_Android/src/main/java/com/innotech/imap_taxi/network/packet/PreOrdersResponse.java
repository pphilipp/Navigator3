package com.innotech.imap_taxi.network.packet;

import java.util.ArrayList;
import java.util.List;

import com.innotech.imap_taxi.datamodel.PreliminaryOrder;
import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 14.11.13
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public class PreOrdersResponse extends Packet {
    public List<PreliminaryOrder> orders;

    public PreOrdersResponse(byte[] data) {
        super(PREORDERS_RESPONCE);

        orders = new ArrayList<PreliminaryOrder>();

        parse(data);
    }

    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //Находим количество заказов
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int ordersCount = Utils.byteToInt(buffer4);

        DispOrderResponse4 tmp;
        int dataSize, count;
        String signed;
        byte[] buffer;

        //Перебераем все заказы
        for (int i = 0 ; i < ordersCount; ++i)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            dataSize = Utils.byteToInt(buffer4);

            tmp = new DispOrderResponse4(DispOrderResponse4.ORDER_RESPONCE4);
            tmp.createOrder();
            tmp.parse(data, offset);
            offset += dataSize;

            orders.add(new PreliminaryOrder(tmp.getOrder()));
//            //ПОдписавшиеся позывные
//            signed = "";
//            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//            offset += buffer4.length;
//            count = Utils.byteToInt(buffer4);

//            for (int j = 0; j < count; ++j)
//            {
//                System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//                offset += buffer4.length;
//                dataSize = Utils.byteToInt(buffer4);
//                buffer = new byte[dataSize];
//                System.arraycopy(data, offset, buffer, 0, buffer.length);
//                offset += buffer.length;
//                signed += StringUtils.bytesToStr(buffer);
//
//                if (j != (count - 1))
//                {
//                    signed += ",";
//                }
//            }
//
//            tmp.getOrder().setSigned(signed);

        }

        //Подписавшиеся
        for (int i = 0; i < ordersCount; ++i)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            count = Utils.byteToInt(buffer4);

            for (int j = 0; j < count; ++j)
            {
                System.arraycopy(data, offset, buffer4, 0, buffer4.length);
                offset += buffer4.length;
                dataSize = Utils.byteToInt(buffer4);
                buffer = new byte[dataSize];
                System.arraycopy(data, offset, buffer, 0, buffer.length);
                offset += buffer.length;
                orders.get(i).signed.add(StringUtils.bytesToStr(buffer));
            }
        }

        buffer4 = null;
        buffer = null;
        tmp = null;
    }
}
