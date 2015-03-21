package com.andrewsosa.beacon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jeffrey Kuang on 3/21/2015.
 */
public class BeaconCommentHelper extends SQLiteOpenHelper {

    public static final String TABLE_BEACON_COMMENT = "beacon_comment";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";

    private static final String DATABASE_NAME = "beacon_comment.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String BEACON_COMMENT_TABLE_CREATE = "create table "
            + TABLE_BEACON_COMMENT  + "("
            + COLUMN_ID             + " integer primary key autoincrement"
            + COLUMN_COMMENT        + " text not null"
            + ");";

    public BeaconCommentHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BEACON_COMMENT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BeaconCommentHelper.class.getName(),
                "Upgrading beacon_comment database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACON_COMMENT);
        onCreate(db);
    }
}
