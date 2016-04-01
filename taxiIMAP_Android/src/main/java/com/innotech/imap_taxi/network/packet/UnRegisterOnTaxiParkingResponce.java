/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.Utils;

/**
 *
 * @author Kvest
 */
public class UnRegisterOnTaxiParkingResponce extends Packet
{
    public static final int RESULT_OK = 1;
    
    private int unregistered;

    public UnRegisterOnTaxiParkingResponce(byte[] data)
    {
        super(UNREGISTER_ON_TAXI_PARKING_RESPONCE);
        unregistered = 0;

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //Unregistered
        buffer4[0] = data[offset++];
        buffer4[1] = 0;
        buffer4[2] = 0;
        buffer4[3] = 0;
        unregistered = Utils.byteToInt(buffer4);
        
        buffer4 = null;
    }

    public int getUnregistered()
    {
        return unregistered;
    }
}
