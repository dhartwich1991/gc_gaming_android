package com.jdapplications.gcgaming.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.adapters.RaidMemberListAdapter;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.models.User;
import com.jdapplications.gcgaming.tasks.IsSignedUpTask;
import com.jdapplications.gcgaming.tasks.RaidDetailTask;
import com.jdapplications.gcgaming.tasks.SignOffForRaidTask;
import com.jdapplications.gcgaming.tasks.SignUpForRaidTask;
import com.jdapplications.gcgaming.ui.FloatingActionButton;
import com.jdapplications.gcgaming.utils.DateFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RaidsDetailActivity extends ActionBarActivity implements OnAsyncResultListener, FloatingActionButton.OnCheckedChangeListener {

    private TextView raidTitle;
    private TextView raidDescription;
    private TextView raidLead;
    private TextView raidStart;
    private TextView raidEnd;
    private RecyclerView raidMembers;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<User> raidMembersList;
    private SharedPreferences sharedPref;
    private String raidId;

    private boolean isSignedUp;

    private FloatingActionButton fab;

    private OnAsyncResultListener asyncResultListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raids_detail);
        raidId = getIntent().getStringExtra("raid_id");

        raidTitle = (TextView) findViewById(R.id.raid_name);
        raidDescription = (TextView) findViewById(R.id.raid_description);
        raidLead = (TextView) findViewById(R.id.raid_lead);
        raidStart = (TextView) findViewById(R.id.raid_start);
        raidEnd = (TextView) findViewById(R.id.raid_end);

        mLayoutManager = new LinearLayoutManager(this);
        raidMembers = (RecyclerView) findViewById(R.id.raid_members);
        raidMembers.setLayoutManager(mLayoutManager);
        raidMembers.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton) findViewById(R.id.fab_sign_up);
        fab.setOnCheckedChangeListener(this);
        sharedPref = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);

        setOnAsyncResultListener(this);

        isSignedUp(raidId, String.valueOf(sharedPref.getInt("id", 0)));

        loadRaidDetails(raidId);
        Log.d("RaidID", raidId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_raids_detail, menu);
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

    private void loadRaidDetails(String raidId) {
        new RaidDetailTask(asyncResultListener).execute(raidId);
    }

    private void isSignedUp(String raidId, String userId) {
        new IsSignedUpTask(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    isSignedUp = res.getBoolean("signedup");
                    if (isSignedUp) {
                        fab.setChecked(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(RaidsDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).execute(raidId, userId);
    }

    private void signUpForRaid(final String raidId, String userId) {
        new SignUpForRaidTask(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Toast.makeText(RaidsDetailActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    if (jsonResponse.getInt("code") == 0) {
                        loadRaidDetails(raidId);
                        isSignedUp(raidId, String.valueOf(sharedPref.getInt("id", 0)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(RaidsDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).execute(raidId, userId);
    }

    private void signOffForRaid(final String raidId, String userId) {
        new SignOffForRaidTask(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Toast.makeText(RaidsDetailActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    if (jsonResponse.getInt("code") == 0) {
                        loadRaidDetails(raidId);
                        isSignedUp(raidId, String.valueOf(sharedPref.getInt("id", 0)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(RaidsDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).execute(raidId, userId);
    }

    @Override
    public void onResult(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            //Load Raid
            JSONObject raidInfo = jsonResponse.getJSONObject("raid");
            String name = raidInfo.getString("name");
            String description = raidInfo.getString("description");
            String raidlead = raidInfo.getString("raidlead");
            String start = raidInfo.getString("startdate");
            String end = raidInfo.getString("enddate");

            raidTitle.setText(name);
            getSupportActionBar().setTitle(name);
            raidDescription.setText("Description: "+description);
            raidLead.setText("Raidlead: "+raidlead);
            raidStart.setText("Start: "+DateFormatter.formatJSONISO8601Date(start));
            raidEnd.setText("End: "+DateFormatter.formatJSONISO8601Date(end));

            //Load Raid Members
            raidMembersList = new ArrayList<>();
            JSONArray members = jsonResponse.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject tempMember = members.getJSONObject(i);
                int id = tempMember.getInt("id");
                String username = tempMember.getString("username");
                raidMembersList.add(new User(id, username));
                //Do stuff with member
            }
            RaidMemberListAdapter adapter = new RaidMemberListAdapter(raidMembersList);
            raidMembers.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Load Raid Members
        raidMembersList = new ArrayList<>();

    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(RaidsDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChanged(FloatingActionButton fabView, boolean isChecked) {
        //TODO Sign Up / Off for the selected Raid
        if (isChecked) {
            Log.d("Check changed", "true");
            if (!isSignedUp) {
                signUpForRaid(raidId, String.valueOf(sharedPref.getInt("id", 0)));
            }
        } else {
            Log.d("Check changed", "false");
            if(isSignedUp) {
                signOffForRaid(raidId, String.valueOf(sharedPref.getInt("id", 0)));
            }
        }

    }
}
