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
public class SrvMessageResponce extends Packet
{
    private String message;
    
    public SrvMessageResponce(byte[] data)
    {
        super(SRV_MESSAGE_RESPONCE);
        message = "";
        
        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //Message
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        message = StringUtils.bytesToStr(buffer);

        buffer4 = null;
        buffer = null;
    }

    public String getMessage()
    {
        return message;
    }
}
