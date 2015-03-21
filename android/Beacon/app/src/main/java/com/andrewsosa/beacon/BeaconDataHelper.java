package com.andrewsosa.beacon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jeffrey Kuang on 3/21/2015.
 */
public class BeaconDataHelper extends SQLiteOpenHelper {

    public static final String TABLE_BEACON_DATA = "beacon_data";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BEACON_ID = "beacon_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_RATING = "rating";

    private static final String DATABASE_NAME = "beacon_data.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String BEACON_DATA_TABLE_CREATE = "create table "
            + TABLE_BEACON_DATA + "("
            + COLUMN_ID         + " integer primary key autoincrement"
            + COLUMN_BEACON_ID  + " integer not null"
            + COLUMN_TYPE       + " text not null"
            + COLUMN_TAG        + " text not null"
            + COLUMN_COMMENT    + " text not null"
            + COLUMN_RATING     + " integer not null"
            + ");";

    public BeaconDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BEACON_DATA_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BeaconDataHelper.class.getName(),
                "Upgrading beacon_data database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACON_DATA);
        onCreate(db);
    }
}
