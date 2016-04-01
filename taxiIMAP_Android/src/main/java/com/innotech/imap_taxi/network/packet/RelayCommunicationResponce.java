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
public class RelayCommunicationResponce extends Packet
{
    public static final String REGISTER_TYPE = "Register";
    public static final String UNREGISTER_TYPE = "UnRegister";
    
    public static final String AT_INVALID_CALLSIGN = "InvalidCallSign";
    public static final String AT_OUT_OF_SEASON = "OutOfSeason";
    public static final String AT_DRIVER_IS_BLOCKED = "DriverIsBlocked";
    
    private String relayCommunicationType;
    private String relayAnswerType;
    private int relayID;
    private long openRelayDate;
    private int fromDriver;
    
    public RelayCommunicationResponce(byte[] data)
    {
        super(RELAY_COMMUNICATION_RESPONCE);
        relayCommunicationType = "";
        relayAnswerType = "";
        relayID = -1;
        openRelayDate = 0;
        fromDriver = -1;

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //RelayCommunicationType
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        relayCommunicationType = StringUtils.bytesToStr(buffer);

        //RelayAnswerType
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        relayAnswerType = StringUtils.bytesToStr(buffer);

        //RelayID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        relayID = Utils.byteToInt(buffer4);

        //OpenRelayDate
        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        openRelayDate = Utils.byteToDate(buffer);

        //FromDriver
        buffer4[0] = data[offset++];
        buffer4[1] = 0; buffer4[2] = 0; buffer4[3] = 0;
        fromDriver = Utils.byteToInt(buffer4);

        buffer4 = null;
        buffer = null;
    }

    public String getRelayCommunicationType()
    {
        return relayCommunicationType;
    }

    public String getRelayAnswerType()
    {
        return relayAnswerType;
    }

    public int getRelayID()
    {
        return relayID;
    }

    public long getOpenRelayDate()
    {
        return openRelayDate;
    }

    public int getFromDriver()
    {
        return fromDriver;
    }

    @Override
    public String toString() {
        return "RelayCommunicationResponce{" +
                "relayCommunicationType='" + relayCommunicationType + '\'' +
                ", relayAnswerType='" + relayAnswerType + '\'' +
                ", relayID=" + relayID +
                ", openRelayDate=" + openRelayDate +
                ", fromDriver=" + fromDriver +
                '}';
    }
}
