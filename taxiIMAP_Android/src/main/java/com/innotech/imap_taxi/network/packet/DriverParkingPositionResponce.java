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
public class DriverParkingPositionResponce extends Packet
{
    public static final int DRIVER_UNREGISTERED = 0;
    
    private int registeredOnParking;
    private int uid;
    private int position;
    private String queue;

    @Override
    public String toString() {
        return "DriverParkingPositionResponce{" +
                "registeredOnParking=" + registeredOnParking +
                ", uid=" + uid +
                ", position=" + position +
                ", queue='" + queue + '\'' +
                '}';
    }

    public DriverParkingPositionResponce(byte[] data)
    {
        super(DRIVER_PARKING_POSITION_RESPONCE);
        registeredOnParking = 0;
        uid = -1;
        position = -1;
        queue = "";
        
        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //Стал ли на стоянку
        buffer4[0] = data[offset++];
        buffer4[1] = 0;
        buffer4[2] = 0;
        buffer4[3] = 0;
        registeredOnParking = Utils.byteToInt(buffer4);
        
        //UID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        uid = Utils.byteToInt(buffer4);
        
        //Position
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        position = Utils.byteToInt(buffer4);
        
        buffer4 = null;
    }

    public int getRegisteredOnParking()
    {
        return registeredOnParking;
    }

    public int getUID()
    {
        return uid;
    }

    public int getPosition()
    {
        return position;
    }

    public String getQueue()
    {
        return queue;
    }
}
