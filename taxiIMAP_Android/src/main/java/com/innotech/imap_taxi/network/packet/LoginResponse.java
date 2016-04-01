package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.09.13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class LoginResponse extends Packet {
    public static final String ANSWER_OK = "AllIsInOrder";
    public static final String ANSWER_NORIGHTS = "NoRights";

    public int srvID;
    public int peopleID;
    public String answer;
    public byte[] GUID;

    public LoginResponse(byte[] data) {
        super(LOGIN_RESPONCE);
        srvID = -1;
        peopleID = -1;
        answer = "";
        GUID = new byte[16];
        mPacketNumber = -1;

        parse(data);
    }

    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //SrvID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        srvID = Utils.byteToInt(buffer4);

        //PeopleID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        peopleID = Utils.byteToInt(buffer4);

        //Answer
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        answer = StringUtils.bytesToStr(buffer);

        //GUID
        System.arraycopy(data, offset, GUID, 0, GUID.length);
        offset += GUID.length;

        //PacketNumber
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        mPacketNumber = Utils.byteToInt(buffer4);

        buffer4 = null;
        buffer = null;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "srvID=" + srvID +
                ", peopleID=" + peopleID +
                ", answer='" + answer + '\'' +
                ", GUID=" + Arrays.toString(GUID) +
                '}';
    }
}
