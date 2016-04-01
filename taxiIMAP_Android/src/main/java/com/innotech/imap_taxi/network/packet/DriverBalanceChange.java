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
public class DriverBalanceChange extends Packet
{
    private String reason;
    private String amount;
    
    public DriverBalanceChange(byte[] data)
    {
        super(DRIVER_BALANCE_CHANGED_RESPONCE);
        reason = "";
        amount = "";
        
        parse(data);
    }
    
    protected void parse(byte[] data)
    {
        int offset = 0;
        
        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //reason
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        reason = StringUtils.bytesToStr(buffer);
        
        //amount
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        amount = StringUtils.bytesToStr(buffer);
        
        buffer4 = null;
        buffer = null;
    }

    public String getAmount() 
    {
        return amount;
    }

    public String getReason() 
    {
        return reason;
    }
    
}
