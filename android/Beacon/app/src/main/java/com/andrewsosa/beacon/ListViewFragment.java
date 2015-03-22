package com.andrewsosa.beacon;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BeaconFragment {

    public static String TAG = "LIST_VIEW_FRAGMENT";

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // These are the Beacon rows that we will retrieve
    static final String[] fromColumns = new String[] {
            BeaconHelperData.COLUMN_ID,
            BeaconHelperData.COLUMN_NAME,
            BeaconHelperData.COLUMN_TYPE,
            BeaconHelperData.COLUMN_RATING
    };

    // This is the select criteria
    static final int[] toViews = new int[] {
            R.id.hidden_id,
            R.id.tile_name,
            R.id.tile_type,
            R.id.tile_rating
    };


    public ListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Create a progress bar to display while the list loads
        /*ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar); */
        // For the cursor adapter, specify which columns go into which views

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(getActivity(),
                            R.layout.list_view_tile,
                            ((MainActivity)getActivity()).getDataCursor(),
                            fromColumns, toViews, 0);
        //setListAdapter(mAdapter);
        getListView().setAdapter(mAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), AdminActivity.class);
                String temp = ((TextView)view.findViewById(R.id.hidden_id)).getText().toString();
                long _id = Long.parseLong(temp);
                intent.putExtra("id", _id);
                getActivity().startActivity(intent);

            }
        });

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        //getLoaderManager().initLoader(0, null, this);


    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
        //mAdapter.swapCursor(this.activity.getDataCursor());
    }

    private ListView getListView() {
        return (ListView) getView().findViewById(R.id.list);
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                PROJECTION, SELECTION, null, null);    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }*/

    @Override
    public void updateDataSet(Cursor c) {
        getListView().setAdapter(new SimpleCursorAdapter(getActivity(),
                R.layout.list_view_tile, c,
                fromColumns, toViews, 0));
    }

    @Override
    public void updateDataSet(ArrayList<Beacon> list) {

    }
}
