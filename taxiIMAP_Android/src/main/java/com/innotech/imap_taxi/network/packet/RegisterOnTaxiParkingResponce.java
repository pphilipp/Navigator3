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
public class RegisterOnTaxiParkingResponce extends Packet
{
    public static final String RESULT_DONE = "Done";
    public static final String RESULT_NOT_FOUND = "ParkingNotFound";
    public static final String RESULT_ERROR = "Error";

    private int parkingUID;
    private String parkingName;
    private String result;
    private int numberInQueue;
    
    public RegisterOnTaxiParkingResponce(byte[] data)
    {
        super(REGISTER_ON_TAXI_PARKING_RESPONCE);
        parkingUID = -1;
        parkingName = "";
        result = "";
        numberInQueue = -1;

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //ParkingUID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        parkingUID = Utils.byteToInt(buffer4);
        
        //ParkingName
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        parkingName = StringUtils.bytesToStr(buffer);

        //Result
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        result = StringUtils.bytesToStr(buffer);

        //Номер в очереди
        if ((offset + 3) < data.length)
        {
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            numberInQueue = Utils.byteToInt(buffer4);
        }

        buffer4 = null;
        buffer = null;
    }

    public int getParkingUID()
    {
        return parkingUID;
    }

    public int getNumberInQueue()
    {
        return numberInQueue;
    }

    public String getParkingName()
    {
        return parkingName;
    }

    public String getResult()
    {
        return result;
    }

    @Override
    public String toString() {
        return "RegisterOnTaxiParkingResponce{" +
                "parkingUID=" + parkingUID +
                ", parkingName='" + parkingName + '\'' +
                ", result='" + result + '\'' +
                ", numberInQueue=" + numberInQueue +
                '}';
    }
}
