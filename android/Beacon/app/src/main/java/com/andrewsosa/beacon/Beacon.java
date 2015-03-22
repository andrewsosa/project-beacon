package com.andrewsosa.beacon;

/**
 * Created by andrewsosa on 3/21/15.
 */
public class Beacon {

    private long _id;
    private double longitude;
    private double latitude;
    private String name;
    private String type;
    private int rating;

    public Beacon(long _id, double longitude, double latitude, String name) {
        this._id = _id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
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
