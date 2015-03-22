package com.andrewsosa.beacon;

/**
 * Created by andrewsosa on 3/21/15.
 */
public class Beacon {

    private long _id;
    private double latitude;
    private double longitude;
    private String name;
    private String type;
    private int rating;

    public Beacon(long _id, double latitude, double longitude, String name) {
        this._id = _id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public void setBeacon(long _id, double latitude, double longitude, String name, String type, int rating) {
        this._id = _id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.type = type;
        this.rating = rating;
    }

    public long get_id() {
        return _id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
