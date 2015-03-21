package com.andrewsosa.beacon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jeffrey Kuang on 3/21/2015.
 */
public class BeaconTagHelper extends SQLiteOpenHelper {

    public static final String TABLE_BEACON_TAG = "beacon_tag";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TAG = "tag";


    private static final String DATABASE_NAME = "beacon_data.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String BEACON_TAG_TABLE_CREATE = "create table "
            + TABLE_BEACON_TAG  + "("
            + COLUMN_ID         + " integer primary key autoincrement"
            + COLUMN_TAG        + " text not null"
            + ");";

    public BeaconTagHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BEACON_TAG_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BeaconTagHelper.class.getName(),
                "Upgrading beacon_tag database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACON_TAG);
        onCreate(db);
    }
}
