package com.andrewsosa.beacon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jeffrey Kuang on 3/21/2015.
 */
public class BeaconDataSource {

    // Database fields
    private SQLiteDatabase database;
    private BeaconOpenHelper dbHelper;

    // Column
    private String[] allColumns = {
            BeaconOpenHelper.COLUMN_ID,
            BeaconOpenHelper.COLUMN_LATITUDE,
            BeaconOpenHelper.COLUMN_LONGITUDE,
            BeaconOpenHelper.COLUMN_NAME
    };

    public BeaconDataSource(Context context) {
        dbHelper = new BeaconOpenHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Beacon add_beacon(long Long, long lat, String name) {

        // Pack tuple values
        ContentValues values = new ContentValues();
        values.put(BeaconOpenHelper.COLUMN_NAME, name);
        values.put(BeaconOpenHelper.COLUMN_LONGITUDE, Long);
        values.put(BeaconOpenHelper.COLUMN_LATITUDE, lat);

        // Do insert, get _id
        Long insertId = database.insert(BeaconOpenHelper.TABLE_BEACONS, null, values);

        // Reread the tuple
        Cursor cursor = database.query(BeaconOpenHelper.TABLE_BEACONS,
                allColumns, BeaconOpenHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        // Convert tuple to java object
        cursor.moveToFirst();
        Beacon newList = cursor_to_beacon(cursor);
        cursor.close();
        return newList;
    }

    public void update_beacon(Beacon beacon) {
        long id = beacon.get_id();

        ContentValues values = new ContentValues();
        values.put(BeaconOpenHelper.COLUMN_NAME, beacon.getName());

        database.update(BeaconOpenHelper.TABLE_BEACONS,
                values, BeaconOpenHelper.COLUMN_ID + " = " + id, null);
    }

    public void delete_beacon(Beacon beacon) {
        long id = beacon.get_id();
        database.delete(BeaconOpenHelper.TABLE_BEACONS, BeaconOpenHelper.COLUMN_ID
            + " = " + id, null);
    }

    public Beacon get_beacon(long id) {

        // Read the tuple
        Cursor cursor = database.query(BeaconOpenHelper.TABLE_BEACONS,
                allColumns, BeaconOpenHelper.COLUMN_ID + " = " + id, null,
                null, null, null);

        // Convert tuple to java object
        cursor.moveToFirst();
        Beacon newBeacon = cursor_to_beacon(cursor);
        cursor.close();
        return newBeacon;
    }

    public ArrayList<Beacon> get_all_beacon() {
        ArrayList<Beacon> beacons = new ArrayList<Beacon>();

        Cursor c = database.query(BeaconOpenHelper.TABLE_BEACONS, allColumns,
                null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            Beacon b = cursor_to_beacon(c);
            beacons.add(b);
            c.moveToNext();
        }

        c.close();
        return beacons;
    }

    public Cursor get_cursor() {
        return database.query(BeaconOpenHelper.TABLE_BEACONS, allColumns,
                null, null, null, null, null);
    }

    private Beacon cursor_to_beacon(Cursor c) {
        return new Beacon(c.getLong(0), c.getLong(1), c.getLong(2), c.getString(3));
    }
}

