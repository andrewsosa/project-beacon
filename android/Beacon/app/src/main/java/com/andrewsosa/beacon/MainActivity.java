package com.andrewsosa.beacon;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity implements Toolbar.OnMenuItemClickListener{

    // Actionbar and Navdrawer nonsense
    ActionBarDrawerToggle mDrawerToggle;
    String[] fragments;
    ListView mDrawerList;
    DrawerLayout drawerLayout;

    // Fragments
    MapViewFragment mapViewFragment;


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

        // Add initial fragment
        if (findViewById(R.id.content_frame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {
                // Create a new Fragment to be placed in the activity layout
                mapViewFragment = new MapViewFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                //firstFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, mapViewFragment).commit();
            }
        }







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
        Fragment fragment = null;

        if (position == 0) {
            fragment = new MapViewFragment();
        } else if (position == 1) {
            fragment = new ListViewFragment();
        }

        Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(mDrawerList);
    }


}
