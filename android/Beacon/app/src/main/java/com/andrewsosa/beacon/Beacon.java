package com.andrewsosa.beacon;

/**
 * Created by andrewsosa on 3/21/15.
 */
public class Beacon {

    private long _id;
    private long longitude;
    private long latitude;
    private String name;

    public Beacon(long _id, long longitude, long latitude, String name) {
        this._id = _id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

    public long get_id() {
        return _id;
    }

    public long getLongitude() {
        return longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }
}
