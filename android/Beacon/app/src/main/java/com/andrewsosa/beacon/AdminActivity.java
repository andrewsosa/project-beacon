package com.andrewsosa.beacon;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class AdminActivity extends ActionBarActivity {
;
    private Beacon editEntry;
    private Long selected_id;
    private EditText beaconName = (EditText) findViewById(R.id.editName);
    private EditText latitude = (EditText) findViewById(R.id.editLatitude);
    private EditText longitude = (EditText) findViewById(R.id.editLongitude);
    private Spinner beaconType = (Spinner) findViewById(R.id.editType);
    private TextView beaconId = (TextView) findViewById(R.id.editId);

    BeaconDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Intent intent = getIntent();
        selected_id = intent.getLongExtra("id", -1);
        if(selected_id == -1) {
            finish();
        }

        ArrayAdapter dataAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getTextArray(R.array.types));
        //dataAdapter.setDropDownViewResource(android)
        beaconType.setAdapter(dataAdapter);

        dataSource = new BeaconDataSource(this);
        dataSource.open();

        editEntry = dataSource.get_beacon(selected_id);
        beaconName.setText(editEntry.getName());
        latitude.setText(""+editEntry.getLatitude());
        longitude.setText(""+editEntry.getLongitude());
        beaconId.setText(""+beaconId.getId());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            editEntry.setBeacon(selected_id, Double.parseDouble(latitude.getText().toString()),
                Double.parseDouble(longitude.getText().toString()),beaconName.getText().toString(),
                beaconType.toString(),0);

            return true;
        }
        if (id == R.id.action_save) {
            dataSource.delete_beacon(editEntry);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
