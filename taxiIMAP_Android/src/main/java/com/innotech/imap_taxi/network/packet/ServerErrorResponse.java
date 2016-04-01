package com.innotech.imap_taxi.network.packet;

import android.util.Log;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.09.13
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class ServerErrorResponse extends Packet {
    public ServerErrorResponse(byte[] data) {
        super(SERVER_ERROR_RESPONCE);

        parse(data);
    }

    protected void parse(byte[] data) {
    	 int offset = 0;

         //Пропускаем название пакета
         byte[] buffer4 = new byte[4];
         System.arraycopy(data, offset, buffer4, 0, buffer4.length);
         offset += buffer4.length + Utils.byteToInt(buffer4);

         System.arraycopy(data, offset, buffer4, 0, buffer4.length);
         offset += buffer4.length;
         int strSize = Utils.byteToInt(buffer4);
         byte[] buffer = new byte[strSize];
         System.arraycopy(data, offset, buffer, 0, buffer.length);
         offset += buffer.length;

         Log.d("tag-tag-tag error_response", StringUtils.bytesToStr(buffer));
    }
}
