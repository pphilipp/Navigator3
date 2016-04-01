package com.innotech.imap_taxi.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.09.13
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
class BuilderStream extends ByteArrayOutputStream {
    private static final int PROTOCOL_VERSION = 1;

    private boolean addProtocolVersion;

    public BuilderStream(String packetName, boolean addProtocolVersion) throws IOException {
        super();

        this.addProtocolVersion = addProtocolVersion;

        //Заглушка для размера пакета
        write((new byte[]{0, 0, 0, 0}));

        //Версия протокола
        if (addProtocolVersion) {
            byte[] protocolVersionData = Utils.int32ToByte(PROTOCOL_VERSION);
            write(protocolVersionData);
        }

        //Имя пакета
        write(packetName);
    }

    public void write(String value) throws IOException {
        byte[] valueData = StringUtils.strToBytes(value);
        write(Utils.int32ToByte(valueData.length));
        write(valueData);
    }

    public void write(long value) throws IOException {
        write(Utils.longToByte(value));
    }

    public void writeInt16(int value) throws IOException {
        write(Utils.int16ToByte(value));
    }

    public void writeInt32(int value) throws IOException {
        write(Utils.int32ToByte(value));
    }

    public void writeLong(long value) throws IOException {
        write(Utils.longToByte(value));
    }

    public void writeDate(long value) throws IOException {
        write(Utils.dateToByte(value));
    }

    public void writeFloat(float value) throws IOException {
        write(Utils.floatToByte(value));
    }

    public void write(boolean value) throws IOException {
        write(Utils.boolToByte(value));
    }

    public /*synchronized*/ byte[] toByteArray() {
        byte[] result = super.toByteArray();

        //Устанавливаем размер пакета
        byte[] protocolVersionData = Utils.int32ToByte(result.length - 4 - (addProtocolVersion ? 4 : 0)); // 4 + 4 = Размер + версия протокола - их не учитываем в размере пакета
        for (int i = 0; i < 4; ++i) {
            result[i] = protocolVersionData[i];
        }

        return result;
    }
}
