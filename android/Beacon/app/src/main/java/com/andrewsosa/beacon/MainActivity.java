package com.andrewsosa.beacon;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends Activity implements Toolbar.OnMenuItemClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // Actionbar and Navdrawer nonsense
    ActionBarDrawerToggle mDrawerToggle;
    String[] fragments;
    ListView mDrawerList;
    DrawerLayout drawerLayout;

    // Fragments
    MapViewFragment mapViewFragment;

    // Data source
    BeaconDataSource dataSource;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    // Active Fragment
    BeaconFragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar craziness
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        // Drawer craziness
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);

        // Init location cool stuff
        buildGoogleApiClient();

        // Open the datasource
        dataSource = new BeaconDataSource(this);
        dataSource.open();

        // FAB listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBeaconDialog();
            }
        });
        fab.requestFocus();

        // Search bar listener
        EditText editText = (EditText) findViewById(R.id.searchBar);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (v.getText().toString().length() > 0) {

                        if (activeFragment instanceof MapViewFragment) {
                            activeFragment.updateDataSet(CursorToList(dataSource.search_data(v.getText().toString())));
                            //Toast.makeText(MainActivity.this, "Searched map.", Toast.LENGTH_SHORT).show();

                        } else if (activeFragment instanceof ListViewFragment) {
                            activeFragment.updateDataSet(dataSource.search_data(v.getText().toString()));
                            //Toast.makeText(MainActivity.this, "Searched List.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "No match.", Toast.LENGTH_SHORT).show();
                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                }
                return false;
            }
        });

        // Add initial fragment
        if (findViewById(R.id.content_frame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {
                // Create a new Fragment to be placed in the activity layout
                activeFragment = new MapViewFragment();
                activeFragment.setActivity(this);

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                //firstFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, activeFragment).commit();
            }
        }

        // This is drawer touch stuff
        RelativeLayout map_group = (RelativeLayout) findViewById(R.id.map_group);
        map_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(0);
            }
        });

        RelativeLayout list_group = (RelativeLayout) findViewById(R.id.list_group);
        list_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(1);
            }
        });

        /*
        fragments = new String[] {"Map", "List"};
        mDrawerList = (ListView) findViewById(R.id.nav_listView);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_tile, R.id.tile_text, fragments));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Helo", Toast.LENGTH_SHORT).show();
                selectItem(position);
            }
        }); */

    }

    /* This displays the drawer toggler. */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /* Handles voice search toggle */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        
        // Create a new fragment and specify the planet to show based on position
        //BeaconFragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();

        if (position == 0 && (fragmentManager.findFragmentByTag(MapViewFragment.TAG) == null
                ||  !fragmentManager.findFragmentByTag(MapViewFragment.TAG).isVisible())) {
            activeFragment = new MapViewFragment();
        } else if (position == 1 && (fragmentManager.findFragmentByTag(ListViewFragment.TAG) == null
                || !fragmentManager.findFragmentByTag(ListViewFragment.TAG).isVisible())) {
            activeFragment = new ListViewFragment();
        }

        // Insert the fragment by replacing any existing fragment
        if (activeFragment != null) {
            activeFragment.setActivity(this);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, activeFragment)
                    .commit();
        }
        // Highlight the selected item, update the title, and close the drawer
        //mDrawerList.setItemChecked(position, true);
        //drawerLayout.closeDrawer(drawerLayout);
        drawerLayout.closeDrawers();
    }

    public Cursor getDataCursor() {
        return dataSource.get_cursor();
    }

    public ArrayList<Beacon> getBeaconList() {
        return dataSource.get_all_beacon();
    }

    private EditText nameInput;
    private View positiveAction;
    private void createBeaconDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.create_dialog_view, false)
                .title("Create a new Beacon")
                .positiveText("Create")
                .negativeText(android.R.string.cancel)
                .positiveColor(getResources().getColor(R.color.primaryColor))
                .negativeColor(getResources().getColor(R.color.primaryColor))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (nameInput != null) {
                            dataSource.add_beacon(location.getLatitude(),
                                    location.getLongitude(),
                                    nameInput.getText().toString());
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameInput = (EditText) dialog.getCustomView().findViewById(R.id.list_name_input);
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        /*if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /*protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }*/

    private ArrayList<Beacon> CursorToList(Cursor c) {
        ArrayList<Beacon> beacons = new ArrayList<Beacon>();

        c.moveToFirst();
        while (!c.isAfterLast()) {
            Beacon b = BeaconDataSource.cursor_to_beacon(c);
            beacons.add(b);
            c.moveToNext();
        }

        c.close();
        return beacons;
    }
}
