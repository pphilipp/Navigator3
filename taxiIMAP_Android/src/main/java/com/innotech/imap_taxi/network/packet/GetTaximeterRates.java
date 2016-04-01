package com.innotech.imap_taxi.network.packet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.innotech.imap_taxi.network.StringUtils;
import com.innotech.imap_taxi.network.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class GetTaximeterRates extends Packet {
	JSONArray classes;
    public GetTaximeterRates(int id)
    {
        super(id);
    }

    public GetTaximeterRates(byte[] data)
    {
        super(TAXIMETER_RATES);
        classes=new JSONArray();
        parse(data);
    }

    public JSONArray getClasses() {
        return classes;
    }
    @Override
    protected void parse(byte[] data) {
        int offset = 0;

        //Пропускаем название пакета
        byte[] buffer4 = new byte[4];
        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length + Utils.byteToInt(buffer4);

        System.arraycopy(data, offset, buffer4, 0, buffer4.length);
        offset += buffer4.length;
        int strSize = Utils.byteToInt(buffer4);
        byte[] buffer = new byte[strSize];
        System.arraycopy(data, offset, buffer, 0, buffer.length);
        offset += buffer.length;
        try {
            JSONObject job = new JSONObject(StringUtils.bytesToStr(buffer));
			classes = job.getJSONArray("taximeters");
	//		Toast.makeText(ContextHelper.getInstance().getCurrentContext(), classes.toString(), Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        buffer=null;
    }
}
