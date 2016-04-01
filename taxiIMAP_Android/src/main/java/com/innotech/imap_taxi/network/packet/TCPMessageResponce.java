package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

public class TCPMessageResponce extends Packet {

    public int orderID;
    public String messageType, message, from, to;
    public long sendDate;
    public String className;

    public TCPMessageResponce(byte[] data) {
        super(TCPMESSAGE_RESPONCE);
        orderID = -1;
        className = "";

        parse(data);
    }
    
    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //������ �������� - MesType
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        messageType = StringUtils.bytesToStr(buffer);
        System.out.println("messageType " + messageType);
        
        //Message
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        message = StringUtils.bytesToStr(buffer);
        System.out.println("message " + message);

        //From
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        from = StringUtils.bytesToStr(buffer);
        System.out.println("from " + from);

        //To
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        strSize = Utils.byteToInt(buffer4);
        buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        to = StringUtils.bytesToStr(buffer);
        System.out.println("to " + to);

        //����� �������� - ���a
        buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        sendDate = Utils.byteToDate(buffer);
        System.out.println("sendDate " + sendDate);

        //BinData
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int size = Utils.byteToInt(buffer4);
        if (size > 0)
        {
            //className
            System.arraycopy(data, offset, buffer4, 0, buffer4.length);
            offset += buffer4.length;
            strSize = Utils.byteToInt(buffer4);
            buffer = new byte[strSize];
            System.arraycopy(data, offset, buffer, 0, buffer.length);
            offset += buffer.length;
            className = StringUtils.bytesToStr(buffer);
            System.out.println("className " + className);

            //if (className.equals("IMAP.Net.OrderIsYours_mesbindata")||className.equals("IMAP.Net.OrderIsNotYours_mesbindata"))
            if (!className.equals(""))
            {
                System.arraycopy(data, offset, buffer4, 0, buffer4.length);
                offset += buffer4.length;
                orderID = Utils.byteToInt(buffer4);
                System.out.println("orderID " + orderID);
            }
        }

        buffer4 = null;
        buffer = null;
    }

//    protected void parse(byte[] data) {
//        int offset = 0;
//
//        //Пропускаем название пакета
//        byte[] buffer4 = new byte[4];
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length + Utils.byteToInt(buffer4);
//        
//        //messageType
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        byte[] buffer = new byte[Utils.byteToInt(buffer4)];
//        System.arraycopy(data, offset, buffer, 0, buffer.length);
//        offset += buffer.length;
//        messageType = StringUtils.bytesToStr(buffer);
//        System.out.println(offset + " message " +  message);
//        
//        //message
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        buffer = new byte[Utils.byteToInt(buffer4)];
//        System.arraycopy(data, offset, buffer, 0, buffer.length);
//        offset += buffer.length;
//        message = StringUtils.bytesToStr(buffer);
//        System.out.println(offset + " message " +  message);
//        
//        //from
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        buffer = new byte[Utils.byteToInt(buffer4)];
//        System.arraycopy(data, offset, buffer, 0, buffer.length);
//        offset += buffer.length;
//        from = StringUtils.bytesToStr(buffer);
//        System.out.println(offset + " from " + from);
//        
//        //to
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        buffer = new byte[Utils.byteToInt(buffer4)];
//        System.arraycopy(data, offset, buffer, 0, buffer.length);
//        offset += buffer.length;
//        to = StringUtils.bytesToStr(buffer);
//        System.out.println(offset + " to " + to);
//
//        //sendDate
//        buffer = new byte[8];
//        System.arraycopy(data, offset, buffer, 0, buffer.length);
//        offset += buffer.length;
//        sendDate = Utils.byteToDate(buffer);
//        System.out.println(offset + " sendDate " + sendDate);
//        
//        
//        
//        //nameofpacket (OrderIsNotYours)
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        int size = Utils.byteToInt(new byte[4]);
//        buffer = new byte[size];
//        System.arraycopy(data, offset, buffer, 0, buffer.length);
//        String val = StringUtils.bytesToStr(buffer);
//        System.out.println(offset + " val " + val);
//        
//        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
//        offset += buffer4.length;
//        orderID = Utils.byteToInt(buffer4);
//        System.out.println(offset + " orderID " + orderID);
//        
//        /*orderIsNotYours = new OrderIsNotYours(data[offset]);
//        orderID = orderIsNotYours.orderID;*/
//
//        buffer4 = null;
//        buffer = null;
//    }
}
