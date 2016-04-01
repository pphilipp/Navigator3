/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 *
 * @author Kvest
 */
public class TaxiParkingStatisticResponce extends Packet
{
    public int[] uid;
    // Массив кол-ва водителей на каждой стоянке
    public int[] driversCount;
    // Позывные на каждой стоянке через точку с запятой.
    public String[] driverCallSigns;
    
    public TaxiParkingStatisticResponce(byte[] data)
    {
        super(TAXI_PARKING_STATISTIC_RESPONCE);

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //Количество
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int count =  Utils.byteToInt(buffer4);

        //id
        uid = new int[count];
        for (int j = 0; j < count; j++)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            uid[j] = Utils.byteToInt(buffer4);
        }
        //count
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        count =  Utils.byteToInt(buffer4);

        buffer4[2] = 0;
        buffer4[3] = 0;
        driversCount = new int[count];
        for (int j = 0; j < count; ++j)
        {
            buffer4[0] = data[offset++];
            buffer4[1] = data[offset++];
            driversCount[j] = Utils.byteToInt(buffer4);
        }

        //Список позывных на стоянке
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        count = Utils.byteToInt(buffer4);
        
        byte[] buffer;
        driverCallSigns = new String[count];
        for (int j = 0; j < count; j++)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            buffer = new byte[Utils.byteToInt(buffer4)];
            
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;

            driverCallSigns[j] = StringUtils.bytesToStr(buffer);
        }
        
        buffer4 = null;
        buffer = null;
    }
}
