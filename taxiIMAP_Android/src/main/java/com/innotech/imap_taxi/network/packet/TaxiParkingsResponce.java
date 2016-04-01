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
public class TaxiParkingsResponce extends Packet
{
    // Массив уникальных идентификаторов стоянок
    public int[] uid;
    // Название стоянки
    public String[] parkingName;
    /// Регион стоянки
    public String[] parkingRegion;
    // Время отклика
    public long responseDate;

    public TaxiParkingsResponce(byte[] data)
    {
        super(TAXI_PARKINGS_RESPONCE);

        parse(data);
    }

    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        parse(data, offset);
    }

    protected void parse(byte[] data, int offset)
    {
        byte[] buffer4 = new byte[4];
        
        //Количество
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int count = Utils.byteToInt(buffer4);

        //ID стоянок
        uid = new int[count];
        for (int i = 0; i < count; i++)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            uid[i] = Utils.byteToInt(buffer4);
        }

        //размер следующей коллекции(равен count)
        offset += 4;

        int strSize;
        byte[] buffer;
        
        //Название стоянки
        parkingName = new String[count];
        for (int i = 0; i < count; i++)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            parkingName[i] = StringUtils.bytesToStr(buffer);
        }

        //размер следующей коллекции(равен count)
        offset += 4;

        //Район
        parkingRegion = new String[count];
        for (int i = 0; i < count; i++)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            parkingRegion[i] = StringUtils.bytesToStr(buffer);
        }

        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        responseDate = Utils.byteToDate(buffer);
        
        buffer4 = null;
        buffer = null;
    }
}
