package com.andrewsosa.beacon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jeffrey Kuang on 3/21/2015.
 */
public class BeaconDataSource {

    // Database fields
    private SQLiteDatabase database;
    private BeaconHelperData dbHelper;

    // Column
    private String[] allColumns = {
            BeaconHelperData.COLUMN_ID,
            BeaconHelperData.COLUMN_LATITUDE,
            BeaconHelperData.COLUMN_LONGITUDE,
            BeaconHelperData.COLUMN_NAME,
            BeaconHelperData.COLUMN_TYPE,
            BeaconHelperData.COLUMN_RATING
    };

    public BeaconDataSource(Context context) {
        dbHelper = new BeaconHelperData(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Beacon add_beacon(long latitude, long longitude, String name) {

        // Pack tuple values
        ContentValues values = new ContentValues();
        values.put(BeaconHelperData.COLUMN_LATITUDE, latitude);
        values.put(BeaconHelperData.COLUMN_LONGITUDE, longitude);
        values.put(BeaconHelperData.COLUMN_NAME, name);
        values.put(BeaconHelperData.COLUMN_TYPE, "Misc.");
        values.put(BeaconHelperData.COLUMN_RATING, 0);

        // Do insert, get _id
        long insertId = database.insert(BeaconHelperData.TABLE_BEACONS, null, values);
        Log.d("Beacon", "insertId = " + insertId);

        // Reread the tuple
        Cursor cursor = database.query(BeaconHelperData.TABLE_BEACONS,
                allColumns, BeaconHelperData.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        Log.d("Beacon", "Cursor count = " + cursor.getCount());
        Log.d("Beacon", "Cursor column count = " + cursor.getColumnCount());

        // Convert tuple to java object
        cursor.moveToFirst();
        Beacon beacon = cursor_to_beacon(cursor);
        cursor.close();
        return beacon;
    }

    public void update_beacon(Beacon beacon) {
        long id = beacon.get_id();

        ContentValues values = new ContentValues();
        values.put(BeaconHelperData.COLUMN_NAME, beacon.getName());

        database.update(BeaconHelperData.TABLE_BEACONS,
                values, BeaconHelperData.COLUMN_ID + " = " + id, null);
    }

    public void delete_beacon(Beacon beacon) {
        long id = beacon.get_id();
        database.delete(BeaconHelperData.TABLE_BEACONS, BeaconHelperData.COLUMN_ID
                + " = " + id, null);
    }

    public Beacon get_beacon(long id) {

        // Read the tuple
        Cursor cursor = database.query(BeaconHelperData.TABLE_BEACONS,
                allColumns, BeaconHelperData.COLUMN_ID + " = " + id, null,
                null, null, null);

        // Convert tuple to java object
        cursor.moveToFirst();
        Beacon newBeacon = cursor_to_beacon(cursor);
        cursor.close();
        return newBeacon;
    }

    public ArrayList<Beacon> get_all_beacon() {
        ArrayList<Beacon> beacons = new ArrayList<Beacon>();

        Cursor c = database.query(BeaconHelperData.TABLE_BEACONS, allColumns,
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
        return database.query(BeaconHelperData.TABLE_BEACONS, allColumns,
                null, null, null, null, null);
    }

    private Beacon cursor_to_beacon(Cursor c) {
        return new Beacon(c.getLong(0), c.getLong(1), c.getLong(2), c.getString(3));
    }

    // general search all fields for beacon_data
    public Cursor search_data(String query) {
        return database.query(true, BeaconHelperData.TABLE_BEACONS, new String[] {BeaconHelperData.COLUMN_NAME,
                BeaconHelperData.COLUMN_TYPE}, BeaconHelperData.COLUMN_NAME + " LIKE" + "'%" +
                query + "%' OR " + BeaconHelperData.COLUMN_TYPE  + " LIKE" + "'%" +
                query + "%'", null, null, null, null, null);
    }

    // general search all fields for beacon_tag
    public Cursor search_tag(String query) {
        return database.query(true, BeaconHelperTag.TABLE_BEACON_TAG, new String[] { BeaconHelperTag.COLUMN_TAG},
                BeaconHelperTag.COLUMN_TAG  + " LIKE" + "'%" + query + "%'", null, null, null, null, null);
    }

    // Merge the two general searchs into one
    public Cursor search_merged(String query) {
        Cursor data_cursor = search_data(query);
        Cursor tag_cursor = search_tag(query);
        Cursor [] final_cursor = {data_cursor, tag_cursor};
        return new MergeCursor(final_cursor);
    }
}

