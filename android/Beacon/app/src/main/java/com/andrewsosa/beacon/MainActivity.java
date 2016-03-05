package com.andrewsosa.beacon;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends Activity implements Toolbar.OnMenuItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {

    // Actionbar and Navdrawer nonsense
    ActionBarDrawerToggle mDrawerToggle;
    String[] fragmentTitles;
    ListView drawerList;
    DrawerLayout drawerLayout;

    // Fragment reference
    //BeaconFragment activeFragment;
    //MapViewFragment mapViewFragment;
    //ListViewFragment listViewFragment;

    // Data source
    //BeaconDataSource dataSource;

    // Location clients
    GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    // Locations
    Location lastLocation;
    Location currentLocation;
    Location createLocation;
    ParseGeoPoint geoPoint;

    // Location Related metadata
    private final Map<String, Marker> mapMarkers = new HashMap<>();
    boolean requestingLocationUpdates = true;
    String lastUpdateTime;
    boolean mResolvingError = false;

    // Stores the current instantiation of the location client in this object
    //private LocationClient locationClient;

    // Fields for the map radius in feet
    private float radius;
    private float lastRadius;

    // Icons for drawer
    static int[] icons = new int[]{
        R.drawable.ic_map_grey600_24dp,
        R.drawable.ic_reorder_grey600_24dp,
        R.drawable.ic_location_on_grey600_24dp
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateValuesFromBundle(savedInstanceState);

        /*
         *
         *      UI SETUP
         *
         */

        // Toolbar craziness
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);

        // Drawer craziness
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primaryColor));

        // Initialize the fragments
        //mapViewFragment = new MapViewFragment();
        //listViewFragment = new ListViewFragment();

        // Set up navigation list
        fragmentTitles = getResources().getStringArray(R.array.fragments);
        drawerList = (ListView) findViewById(R.id.drawerList);

        // List adapter
        drawerList.setAdapter(new DrawerItemAdapter<>(this,
                R.layout.drawer_list_tile,
                R.id.tile_text,
                getResources().getStringArray(R.array.fragments),
                R.id.tile_icon,
                icons
        ));

        // Header view
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.drawer_header, drawerList, false);
        displayUser(header);
        drawerList.addHeaderView(header, null, false);

        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        selectItem(1);

        // Open the datasource
        //dataSource = new BeaconDataSource(this);
        //dataSource.open();

        // FAB listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createLocation = (currentLocation == null) ? lastLocation : currentLocation;
                if (createLocation == null) {
                    Toast.makeText(MainActivity.this,
                            "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();
                    return;
                }
                createBeaconDialog();
            }
        });
        fab.requestFocus();

        /*
         *
         *      LOCATION LOGIC INIT
         *
         */

        radius = ApplicationBeacon.getSearchDistance();
        lastRadius = radius;

        buildGoogleApiClient();

        //locationRequest = LocationRequest.create();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

    }

    /* This displays the drawer toggler. */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /* Handles voice search toggle */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.action_voice_search:
                Intent i = new Intent(this, ParseTest.class);
                startActivity(i);
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                Intent intent = new Intent(this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Log.d("Beacon", "DrawerItemClickListener.onItemCLick()");
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        //--position;

        BeaconFragment fragment = null;
        switch(position) {
            case 1: fragment = new MapViewFragment();
                break;
            //case 2: fragment = new ListViewFragment();
            //    break;
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }

        //if (position == 1) mapViewFragment.updateLocation(getUsableLocation());

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        changeStatusBarColor(position);
        drawerLayout.closeDrawer(findViewById(R.id.scrimInsetsFrameLayout));
    }

    private void displayUser(ViewGroup header) {
        TextView username = (TextView) header.findViewById(R.id.drawerUsername);
        username.setText(ParseUser.getCurrentUser().getUsername());
    }

    private void changeStatusBarColor(int position){
        switch(position) {
            case 1: drawerLayout.setStatusBarBackgroundColor(
                        getResources().getColor(R.color.primaryColor));
                break;

            default: drawerLayout.setStatusBarBackgroundColor(
                    getResources().getColor(R.color.primaryColorDark));

        }
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

                        //LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        geoPoint = new ParseGeoPoint(createLocation.getLatitude(),
                                createLocation.getLongitude());

                        post(geoPoint, nameInput.getText().toString());

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

    private void post(ParseGeoPoint geoPoint, String name) {
        // Create the parse beacon
        ParseBeacon post = new ParseBeacon();
        post.setLocation(geoPoint);
        String text = name.trim();
        post.setText(text);
        post.setUser(ParseUser.getCurrentUser());

        // Set beacon permissions
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        post.setACL(acl);

        // Async upload
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
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
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (ApplicationBeacon.APPDEBUG) {
            Log.d(ApplicationBeacon.APPTAG, "Connected to location services");
        }

        if (requestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

    }

    public Location getUsableLocation(){
        if(currentLocation != null) {
            return currentLocation;
        } else {
            return lastLocation;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Beacon", "Connection suspended : " + i);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Beacon", "Connection failed.");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMyLocationEnabled(true);
        // Set up the camera change handler
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                // Run the map query
                //doMapQuery();
            }
        });

    }

    /*private void doMapQuery() {

        // Determine user location
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        if (myLoc == null) {
            cleanUpMarkers(new HashSet<String>());
            return;
        }
        // Convert location to point to use in Parse Query
        final ParseGeoPoint myPoint = geoPointFromLocation(myLoc);
        // Set up a query
        ParseQuery<ParseBeacon> mapQuery = ParseBeacon.getQuery();
        // Look for within 100 kilos
        mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
        // "where" query info
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);
        // run query in background
        mapQuery.findInBackground(new FindCallback<ParseBeacon>() {
            @Override
            public void done(List<ParseBeacon> objects, ParseException e) {
                
                // Handle the results
                if (e != null) {
                    if (ApplicationBeacon.APPDEBUG) {
                        Log.d(ApplicationBeacon.APPTAG, "An error occurred while querying for map posts.", e);
                    }
                    return;
                }
                

                if (myUpdateNumber != mostRecentMapUpdate) {
                    return;
                }
                // Posts to show on the map
                Set<String> toKeep = new HashSet<String>();
                
                // Loop through the results of the search
                for (ParseBeacon post : objects) {
                    // Add this post to the list of map pins to keep
                    toKeep.add(post.getObjectId());
                    
                    // Check for an existing marker for this post
                    Marker oldMarker = mapMarkers.get(post.getObjectId());
                    
                    // Set up the map marker's location
                    MarkerOptions markerOpts =
                            new MarkerOptions().position(new LatLng(post.getLocation().getLatitude(), post
                                    .getLocation().getLongitude()));
                    
                    // Set up the marker properties based on if it is within the search radius
                    if (post.getLocation().distanceInKilometersTo(myPoint) > radius * METERS_PER_FEET
                            / METERS_PER_KILOMETER) {
                        
                        // Check for an existing out of range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() == null) {
                                // Out of range marker already exists, skip adding it
                                continue;
                            } else {
                                // Marker now out of range, needs to be refreshed
                                oldMarker.remove();
                            }
                        }
                        
                        // Display a red marker with a predefined title and no snippet
                        markerOpts =
                                markerOpts.title(getResources().getString(R.string.post_out_of_range)).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else {
                        // Check for an existing in range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() != null) {
                                // In range marker already exists, skip adding it
                                continue;
                            } else {
                                // Marker now in range, needs to be refreshed
                                oldMarker.remove();
                            }
                        }
                        // Display a green marker with the post information
                        markerOpts =
                                markerOpts.title(post.getName()).snippet(post.getUser().getUsername())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }
                    // Add a new marker
                    Marker marker = mapFragment.getMap().addMarker(markerOpts);
                    mapMarkers.put(post.getObjectId(), marker);
                    if (post.getObjectId().equals(selectedPostObjectId)) {
                        marker.showInfoWindow();
                        selectedPostObjectId = null;
                    }
                }
                // Clean up old markers.
                cleanUpMarkers(toKeep);
            }
        });
    }*/

    private void finishMapQuery() {
        
    }

    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    private void cleanUpMarkers(Set<String> markersToKeep) {
        for (String objId : new HashSet<>(mapMarkers.keySet())) {
            if (!markersToKeep.contains(objId)) {
                Marker marker = mapMarkers.get(objId);
                marker.remove();
                mapMarkers.get(objId).remove();
                mapMarkers.remove(objId);
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("REQUESTING_LOCATION_UPDATES_KEY",
                requestingLocationUpdates);
        savedInstanceState.putParcelable("LOCATION_KEY", currentLocation);
        savedInstanceState.putString("LAST_UPDATED_TIME_STRING_KEY", lastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            /*if (savedInstanceState.keySet().contains("REQUESTING_LOCATION_UPDATES_KEY")) {
                requestingLocationUpdates = savedInstanceState.getBoolean(
                        "REQUESTING_LOCATION_UPDATES_KEY");
                //setButtonsEnabledState();
            }*/

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains("LOCATION_KEY")) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                currentLocation = savedInstanceState.getParcelable("LOCATION_KEY");
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            /*if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }*/
        }
    }



    /*
 * Constants for location update parameters
 */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    /*
     * Constants for handling location results
     */
    // Conversion from feet to meters
    private static final float METERS_PER_FEET = 0.3048f;

    // Conversion from kilometers to meters
    private static final int METERS_PER_KILOMETER = 1000;

    // Initial offset for calculating the map bounds
    private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;

    // Accuracy for calculating the map bounds
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;

    // Maximum results returned from a Parse query
    private static final int MAX_POST_SEARCH_RESULTS = 20;

    // Maximum post search radius for map in kilometers
    private static final int MAX_POST_SEARCH_DISTANCE = 100;


}
