package com.andrewsosa.beacon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jeffrey Kuang on 3/21/2015.
 */
public class BeaconOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_BEACONS = "beacons";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_NAME = "name";

    private static final String DATABASE_NAME = "beacons.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String BEACONS_TABLE_CREATE = "create table "
            + TABLE_BEACONS     + "("
            + COLUMN_ID         + " integer primary key autoincrement"
            + COLUMN_LONGITUDE  + " real not null"
            + COLUMN_LATITUDE   + " real not null"
            + COLUMN_NAME       + " text not null"
            + ");";

    public BeaconOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BEACONS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BeaconOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
        onCreate(db);
    }
}
