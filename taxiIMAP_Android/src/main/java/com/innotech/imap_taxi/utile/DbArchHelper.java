package com.innotech.imap_taxi.utile;

import com.innotech.imap_taxi.helpers.ContextHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbArchHelper extends SQLiteOpenHelper {

	String LOG_TAG = "DbArchHelper";
	final private static String TABLE_NAME = "order_arch";

	public DbArchHelper(Context context) {
		// конструктор суперкласса
		super(context, "myDB", null, 4);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "-- onCreate database --");
		// создаем таблицу с полями
		db.execSQL("create table order_arch ("
				+ "id integer primary key autoincrement,"
				+ "idorder integer unique not null," + "fulldescline text,"
				+ "fulldesc text," + "fulldescother text," + "date integer"
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.d(LOG_TAG, " --- onUpgrade database from " + oldVersion + " to "
				+ newVersion + " version --- ");

		if (oldVersion == 1 && newVersion == 2) {

			db.execSQL("alter table order_arch add column fulldescother text;");

			// db.beginTransaction();
			// try {
			//
			// db.execSQL("alter table order_arch add column fulldescother text;");
			//
			// db.setTransactionSuccessful();
			// } finally {
			// db.endTransaction();
			// }

		}
		//Added "unique" property for field "idorder"
		if (oldVersion < 2) {
			
			
			final String CREATE_NEW_TMP_TABLE = "create table order_arch_tmp ("
					+ "id integer primary key autoincrement,"
					+ "idorder integer unique not null," + "fulldescline text,"
					+ "fulldesc text," + "fulldescother text," + "date integer"
					+ ");";
			final String DROP_OLD_TABLE = "DROP TABLE " + TABLE_NAME + ";";
			final String CREATE_NEW_TABLE = "create table " + TABLE_NAME + " ("
					+ "id integer primary key autoincrement,"
					+ "idorder integer unique not null," + "fulldescline text,"
					+ "fulldesc text," + "fulldescother text," + "date integer"
					+ ");";
			final String COPY_CONTETNTS_TO_NEW_TABLE = "INSERT INTO " + TABLE_NAME
					+ " (idorder, fulldescline, fulldesc, fulldescother, date)"
					+ " SELECT idorder, fulldescline, fulldesc, fulldescother, date"
					+ " FROM order_arch_tmp;";
			final String DROP_TMP_TABLE = "DROP TABLE order_arch_tmp;";
			db.beginTransaction();
			db.execSQL(CREATE_NEW_TMP_TABLE);
			copyDB(db, TABLE_NAME, "order_arch_tmp");
			db.execSQL(DROP_OLD_TABLE);
			db.execSQL(CREATE_NEW_TABLE);
			db.execSQL(COPY_CONTETNTS_TO_NEW_TABLE);
			db.execSQL(DROP_TMP_TABLE);
			db.endTransaction();
		}

	}

	private void copyDB(SQLiteDatabase db, String oldName, String newName) {
		
		Cursor c = db.query(oldName, null, null, null, null, null, null);
		ContentValues cv = new ContentValues();
		long insertedCount = 0;
		while (c.moveToNext()) {
			cv.put("idorder", c.getInt(c.getColumnIndex("idorder")));
			cv.put("fulldescline", c.getInt(c.getColumnIndex("fulldescline")));
			cv.put("fulldesc", c.getInt(c.getColumnIndex("fulldesc")));
			cv.put("fulldescother", c.getInt(c.getColumnIndex("fulldescother")));
			cv.put("date", c.getInt(c.getColumnIndex("date")));
			long rows = db.insert(newName, null, cv);
			if (rows != -1)
				insertedCount++;
		}
		Log.d("myLogs", "Transmitted " + insertedCount + " of " + c.getCount() + "rows");
	}

}