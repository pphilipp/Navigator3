package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.09.13
 * Time: 20:10
 * To change this template use File | Settings | File Templates.
 */
public class SrvTransferDataResponse extends Packet {
    public byte[] body;

    public SrvTransferDataResponse(byte[] data) {
        super(SRV_TRANSFER_DATA_RESPONCE);
        body = null;

        parse(data);
    }

    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //Какие то ненужные параметры
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        offset += 4 + 4 + 16 + 1; //Не помню откуда эти магические числа, но эти параметры нам не нужны(наверно)

        //Body
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        body = new byte[Utils.byteToInt(buffer4)];
        System.arraycopy(data, offset, body, 0, body.length);
        offset += body.length;

        //mPacketNumber
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        mPacketNumber = Utils.byteToInt(buffer4);

        buffer4 = null;
    }
}
