package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.10.13
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */
public class PingStateAnswer extends Packet {
    public boolean isInRegisterLog;

    public PingStateAnswer(byte[] data) {
        super(PING_STATE_ANSWER);
        isInRegisterLog = false;

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //IsInRegisterLog
        isInRegisterLog = (data[offset++] != 0);
    }
}
