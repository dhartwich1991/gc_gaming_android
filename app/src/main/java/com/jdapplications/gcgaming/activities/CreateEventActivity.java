package com.jdapplications.gcgaming.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.tasks.SaveEventTask;
import com.jdapplications.gcgaming.ui.FloatingActionButton;

public class CreateEventActivity extends ActionBarActivity implements View.OnClickListener, OnAsyncResultListener {

    private FloatingActionButton fab;
    private OnAsyncResultListener asyncResultListener;
    private EditText eventTitle, eventDescription, eventOrga, eventStart, eventEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        fab = (FloatingActionButton) findViewById(R.id.fab_save_event);
        fab.setOnClickListener(this);
        setOnAsyncResultListener(this);

        eventTitle = (EditText) findViewById(R.id.new_event_title);
        eventDescription = (EditText) findViewById(R.id.new_event_desc);
        eventOrga = (EditText) findViewById(R.id.new_event_orga);
        eventStart = (EditText) findViewById(R.id.new_event_start);
        eventEnd = (EditText) findViewById(R.id.new_event_end);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void setOnAsyncResultListener(OnAsyncResultListener listener) {
        this.asyncResultListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_save_event) {
            saveEvent();
        }
    }

    private void saveEvent() {
        String title, desc, orga, start, end;
        title = eventTitle.getText().toString();
        desc = eventDescription.getText().toString();
        orga = eventOrga.getText().toString();
        start = eventStart.getText().toString();
        end = eventEnd.getText().toString();

        new SaveEventTask(asyncResultListener).execute(title, desc, orga, start, end);
        fab.setEnabled(false);
    }

    @Override
    public void onResult(String response) {
        Toast.makeText(CreateEventActivity.this, "Event created", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(CreateEventActivity.this, "Event couldn't be created " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
