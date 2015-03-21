package com.andrewsosa.beacon;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BeaconFragment {

    public static String TAG = "LIST_VIEW_FRAGMENT";

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // These are the Beacon rows that we will retrieve
    static final String[] PROJECTION = new String[] {BeaconHelperData.COLUMN_NAME};

    // This is the select criteria
    static final String SELECTION = null;


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
        String[] fromColumns = {BeaconHelperData.COLUMN_NAME};
        int[] toViews = {R.id.tile_name}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                this.activity.getDataCursor(),
                fromColumns, toViews, 0);
        //setListAdapter(mAdapter);
        getListView().setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        //getLoaderManager().initLoader(0, null, this);


    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
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
}
