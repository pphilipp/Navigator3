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
public class CallSignChangedResponce extends Packet
{
    private int orderID;
    private String newCallSign;

    public CallSignChangedResponce(byte[] data)
    {
        super(CALL_SIGNCHANGED_RESPONCE);
        orderID = -1;
        newCallSign = "";
        
        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //OrderID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        orderID = Utils.byteToInt(buffer4);

        //NewCallSign
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        newCallSign = StringUtils.bytesToStr(buffer);
        
        buffer = null;
        buffer4 = null;
    }

    public int getOrderID()
    {
        return orderID;
    }

    public String getNewCallSign()
    {
        return newCallSign;
    }
}
