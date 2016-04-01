package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 17.11.13
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class DriverBlockedPack extends Packet {
    public long lockWith;
    public long lockTo;
    public String lockDescription;
    public boolean isForever;

    public DriverBlockedPack(byte[] data) {
        super(DRIVER_BLOCKED_PACK);
        lockWith = 0;
        lockTo = 0;
        lockDescription = "";

        parse(data);
    }

    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //LockWith
        byte[] buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        lockWith = Utils.byteToDate(buffer);

        //LockTo
        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        lockTo = Utils.byteToDate(buffer);

        //LockDescription
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        buffer = new byte[Utils.byteToInt(buffer4)];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        lockDescription = StringUtils.bytesToStr(buffer);
        
        //isForever
        isForever = (data[offset++] != 0);

        buffer4 = null;
        buffer = null;
    }
}
