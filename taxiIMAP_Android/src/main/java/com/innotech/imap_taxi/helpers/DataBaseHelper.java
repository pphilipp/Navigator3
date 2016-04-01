package com.innotech.imap_taxi.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.innotech.imap_taxi.model.DriverModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SV_LTD
 * Date: 9/21/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String TAXI_DB = "IMAP";
    public static final String IMAP_TABLE_NAME = "COOKIES";
    public static final String ID = "ID_FIELD";
    public static final String CALLSIGN = "CALLSIGN_FIELD";

    public static final String LAST_NAME = "LAST_NAME_FIELD";
    public static final String NAME = "NAME_FIELD";
    public static final String PASSWORD = "PASSWORD_FIELD";

    public DataBaseHelper(Context context) {
        super(context, TAXI_DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " +IMAP_TABLE_NAME+ " (" +ID+ " integer primary key autoincrement, " +CALLSIGN+ " text, " +NAME+ " text, " +LAST_NAME+ " text, " +PASSWORD+ " text);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static List<String> getCallsigns(Context context) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor c = database.query(IMAP_TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();

        List<String> callsigns = new ArrayList<String>();
        while(c.moveToNext()) {
            callsigns.add(c.getString(1));
        }

        c.close();
        dbHelper.close();
        Log.d(TAXI_DB, callsigns.toString());
        return callsigns;
    }

    public static void deleteContact(Context context, String callsign) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(IMAP_TABLE_NAME, CALLSIGN + " = ?", new String[] { String.valueOf(callsign)});
        db.close();
    }

    public static DriverModel getDriverModelFromCallsign(Context context, String callsign) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        DriverModel driverModel = null;
        Cursor c = database.query(IMAP_TABLE_NAME, null, null, null, null, null, null);

        c.moveToFirst();

        while(c.moveToNext()) {
            if(c.getString(1).equals(callsign)) {
                driverModel = new DriverModel(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4));
            }
        }

        c.close();
        dbHelper.close();
        Log.d(TAXI_DB, driverModel.toString());
        return driverModel;
    }
}
