package com.andrewsosa.beacon;

import android.app.Fragment;

import java.util.ArrayList;

/**
 * Created by andrewsosa on 3/21/15.
 */
public abstract class BeaconFragment extends Fragment {

    MainActivity activity;

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public ArrayList<Beacon> getAllBeacons() {
        return activity.getBeaconList();
    }
}
