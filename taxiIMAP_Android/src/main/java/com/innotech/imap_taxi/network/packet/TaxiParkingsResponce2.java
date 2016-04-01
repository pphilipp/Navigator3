/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.imap_taxi.network.packet;

import com.innotech.imap_taxi.network.Utils;


/**
 *
 * @author Kvest
 */
public class TaxiParkingsResponce2 extends TaxiParkingsResponce
{
    public long changeTaxiParkingsDate;

    public TaxiParkingsResponce2(byte[] data)
    {
        super(data);
        setPacketNumber(TAXI_PARKINGS_RESPONCE2);
    }
    
    protected void parse(byte[] data)
    {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);
        
        //ResponseDate - нам не надо
        //offset += 8;
        
        //ChangeTaxiParkingsDate 
        byte[] buffer = new byte[8];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        changeTaxiParkingsDate = Utils.byteToDate(buffer);

        super.parse(data, offset);

        buffer4 = null;
        buffer = null;
    }
}
