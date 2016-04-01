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
public class PPCSettingsResponce extends Packet
{
    public int minSendCoordsTime;
    public int maxSendCoordsTime;
    public int sendCoordsPerDistance;
    public int registerDriversTimeOut;
    public int T1,T2,T3;
    public String javaVersion;
    public String link;
    public int maxOrdersCount;
    public int autoTariffClassUID;
    public int serverWaitTime;
    public int clientWaitTime;
    public boolean autoCalculatedClientTime;
    public int auctionTime;
    public boolean checkDistToOrderAddr;
    public int minRadiusToOrderAddr;
    public boolean checkNoSubOrderForClose;
    public int maxDistToAddr;
    
    public PPCSettingsResponce(byte[] data)
    {
        super(PPCSETTINGS_RESPONCE);
        T1 = 60000;
        T2 = 60000;
        T3 = 60000;
        javaVersion = "";
        link = "";
        maxOrdersCount = -1;
        autoTariffClassUID = 0;
        serverWaitTime = 60;
        clientWaitTime = 15;
        autoCalculatedClientTime = true;
        auctionTime = 6;
        checkDistToOrderAddr = false;
        minRadiusToOrderAddr = 5000;
        checkNoSubOrderForClose = false;
        maxDistToAddr = 20000;

        parse(data);
    }

    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //MinSendCoordsTime
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        minSendCoordsTime = Utils.byteToInt(buffer4);

        //MaxSendCoordsTime
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        maxSendCoordsTime = Utils.byteToInt(buffer4);

        //SendCoordsPerDistance
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        sendCoordsPerDistance = Utils.byteToInt(buffer4);

        //RegisterDriversTimeOut
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        registerDriversTimeOut = Utils.byteToInt(buffer4);

        //T1
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        T1 = Utils.byteToInt(buffer4);
  
        //T2
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        T2 = Utils.byteToInt(buffer4);
        //T3
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        T3 = Utils.byteToInt(buffer4);

        int strSize;
        byte[] buffer;
        //AutoTariffClassUID
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        autoTariffClassUID = Utils.byteToInt(buffer4);

        //JavaVersion
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        javaVersion = StringUtils.bytesToStr(buffer);

        // Линк на новую версию Java
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        link = StringUtils.bytesToStr(buffer);

        //Кол-во одновременно взятых заказов
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        maxOrdersCount = Utils.byteToInt(buffer4);

        //serverWaitTime;
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        serverWaitTime = Utils.byteToInt(buffer4);

        // clientWaitTime;
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        clientWaitTime = Utils.byteToInt(buffer4);

        //autoCalculatedClientTime;
        autoCalculatedClientTime = (data[offset++] != 0);

        //auctionTime;
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        auctionTime = Utils.byteToInt(buffer4);

        //checkDistToOrderAddr;
        checkDistToOrderAddr = (data[offset++] != 0);

        //minRadiusToOrderAddr;
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        minRadiusToOrderAddr = Utils.byteToInt(buffer4);

        //checkNoSubOrderForClose;
        checkNoSubOrderForClose = (data[offset++] != 0);

        //maxDistToAddr;
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        maxDistToAddr = Utils.byteToInt(buffer4);


        buffer = null;
        buffer4 = null;
    }
}
