package com.andrewsosa.beacon;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by andrewsosa on 4/5/15.
 */

@ParseClassName("Beacon")
public class ParseBeacon extends ParseObject {

    // Beacon name
    public String getName() {
        return getString("name");
    }

    public void setText(String value) {
        put("name", value);
    }

    // Beacon creator "user"
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    // Beacon location
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    // Gets many beacons?
    public static ParseQuery<ParseBeacon> getQuery() {
        return ParseQuery.getQuery(ParseBeacon.class);
    }
}
