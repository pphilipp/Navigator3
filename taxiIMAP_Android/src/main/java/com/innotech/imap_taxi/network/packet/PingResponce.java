/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.innotech.imap_taxi.network.packet;



/**
 *
 * @author Kvest
 */
public class PingResponce extends Packet
{
    public PingResponce(byte[] data)
    {
        super(PING_RESPONCE);

        parse(data);
    }

    protected void parse(byte[] data){
        int offset = 0;

        //Пропускаем название пакета
//        byte[] buffer4 = new byte[4];
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length + Utils.byteToInt(buffer4);
//
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        int strSize = Utils.byteToInt(buffer4);
//        byte[] buffer = new byte[strSize];
//        System.arraycopy(data, offset, buffer, 0, buffer.length);
//        offset += buffer.length;
//        Log.d("KVEST_TAG",StringUtils.bytesToStr(buffer));
    }
}
