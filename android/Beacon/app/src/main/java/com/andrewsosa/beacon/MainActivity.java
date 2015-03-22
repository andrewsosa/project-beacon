package com.andrewsosa.beacon;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;


public class MainActivity extends Activity implements Toolbar.OnMenuItemClickListener{

    // Actionbar and Navdrawer nonsense
    ActionBarDrawerToggle mDrawerToggle;
    String[] fragments;
    ListView mDrawerList;
    DrawerLayout drawerLayout;

    // Fragments
    MapViewFragment mapViewFragment;

    // Data source
    BeaconDataSource dataSource;

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

        // Add initial fragment
        if (findViewById(R.id.content_frame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {
                // Create a new Fragment to be placed in the activity layout
                mapViewFragment = new MapViewFragment();
                mapViewFragment.setActivity(this);

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                //firstFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, mapViewFragment, mapViewFragment.TAG).commit();
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
        BeaconFragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();

        if (position == 0 && (fragmentManager.findFragmentByTag(MapViewFragment.TAG) == null
                ||  !fragmentManager.findFragmentByTag(MapViewFragment.TAG).isVisible())) {
            fragment = new MapViewFragment();
        } else if (position == 1 && (fragmentManager.findFragmentByTag(ListViewFragment.TAG) == null
                || !fragmentManager.findFragmentByTag(ListViewFragment.TAG).isVisible())) {
            fragment = new ListViewFragment();
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            fragment.setActivity(this);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
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

    private EditText nameInput;
    private View positiveAction;
    private void createBeaconDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.create_dialog_view, false)
                .title("List Name")
                .positiveText("Create")
                .negativeText(android.R.string.cancel)
                .positiveColor(getResources().getColor(R.color.primaryColor))
                .negativeColor(getResources().getColor(R.color.primaryColor))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (nameInput != null) {
                            dataSource.add_beacon(0,0,nameInput.getText().toString());
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


}
