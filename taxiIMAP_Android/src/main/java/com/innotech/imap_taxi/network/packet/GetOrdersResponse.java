package com.innotech.imap_taxi.network.packet;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.network.Utils;

public class GetOrdersResponse extends Packet {
    public static final String LOG_TAG = GetOrdersResponse.class.getSimpleName();
    private List<DispOrder4> orders;
    
    public GetOrdersResponse(byte[] data) {
        super(GET_ORDERS_RESPONCE);
        orders = new ArrayList<DispOrder4>();
        
        parse(data);

        Log.d(LOG_TAG,  "orders.size() " + orders.size());
    }
    
    protected void parse(byte[] data) {
    	//System.out.println("data - " + Arrays.toString(data));
    	
        int offset = 0;
        
        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //Если произошла ошибка - ничего не делаем
        if (data[offset++] == 1) {return;}
        
        //Находим количество заказов
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int ordersCount = Utils.byteToInt(buffer4);
      
        DispOrderResponse4 tmp;
        int dataSize;

        //Перебераем все заказы
        for (int i = 0 ; i < ordersCount; ++i) {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            dataSize = Utils.byteToInt(buffer4);

            tmp = new DispOrderResponse4(DispOrderResponse4.ORDER_RESPONCE4);
            tmp.createOrder();
            tmp.parse(data, offset);
            offset += dataSize;

            orders.add(tmp.getOrder());
        }
        
        buffer4 = null;
        tmp = null;
    }
    
    public int count()
    {
        return orders.size();
    }

    public DispOrder4 getOrder(int index)
    {
        return orders.get(index);
    }

    @Override
    public String toString() {
        return "GetOrdersResponse{" +
                "orders=" + orders.toString() +
                '}';
    }
}
