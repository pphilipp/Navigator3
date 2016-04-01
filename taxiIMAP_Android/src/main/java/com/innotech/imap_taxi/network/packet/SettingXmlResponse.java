package com.innotech.imap_taxi.network.packet;

import android.util.Log;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 12.10.13
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class SettingXmlResponse extends Packet {
    private String settings;

    public SettingXmlResponse(byte[] data) {
        super(SETTINGS_XML_RESPONCE);
        Log.d("PACKETS", "SettingsXML data.length = " + data.length);
        parse(data);
    }

    @Override
    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        //settings
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        settings  = StringUtils.bytesToStr(buffer);
        Log.d("PACKETS", "SettingsXML setting.length = " + settings.length());
    }
    
    public String getSettings() {
    	return settings;
    }
}
