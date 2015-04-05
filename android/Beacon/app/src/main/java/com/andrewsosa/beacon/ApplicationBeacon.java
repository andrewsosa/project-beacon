package com.andrewsosa.beacon;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by andrewsosa on 4/5/15.
 */
public class ApplicationBeacon extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "0wkNyK0I2ypejeGidRTNKT1kVvbZR1E9AZN0vqNO", "evTzfSishJhYw7lruCOGj7fy6VsUD5suaohx18qj");

    }
}
