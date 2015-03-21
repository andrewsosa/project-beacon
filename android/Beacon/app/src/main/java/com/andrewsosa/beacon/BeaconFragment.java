package com.andrewsosa.beacon;

import android.app.Fragment;

/**
 * Created by andrewsosa on 3/21/15.
 */
public abstract class BeaconFragment extends Fragment {

    MainActivity activity;

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}
