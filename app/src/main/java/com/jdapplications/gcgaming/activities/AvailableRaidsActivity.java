package com.jdapplications.gcgaming.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.adapters.AvailableRaidsAdapter;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.models.Raid;
import com.jdapplications.gcgaming.tasks.AvailableRaidsTask;
import com.jdapplications.gcgaming.ui.FloatingActionButton;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AvailableRaidsActivity extends ActionBarActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView availableRaidsView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AvailableRaidsAdapter mAdapter;
    private ArrayList<Raid> availableRaids;
    private JSONArray availableRaidsArray;
    private SharedPreferences prefs;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String[] mMenuItems;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_raids);

        mMenuItems = new String[]{"Raids", "Character", "Profile"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getTitle());
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        availableRaidsView = (RecyclerView) findViewById(R.id.availableRaidsView);
        availableRaidsView.setOnClickListener(this);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        availableRaidsView.setLayoutManager(mLayoutManager);
        availableRaids = new ArrayList<>();

        prefs = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);

        fab = (FloatingActionButton) findViewById(R.id.fab_add_event);
        fab.setOnClickListener(this);

        if (!prefs.getBoolean("moderator", false)) {
            fab.setVisibility(View.GONE);
        }

        JodaTimeAndroid.init(AvailableRaidsActivity.this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        availableRaidsView.setItemAnimator(new DefaultItemAnimator());

        loadAvailableRaids();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_available_raids, menu);
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
        } else if (id == R.id.action_logout) {
            //TODO: Log user out and remove Data from Shared Prefs
        } else if (id == android.R.id.home) {
            mDrawerToggle.onOptionsItemSelected(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add_event) {
            startActivityForResult(new Intent(AvailableRaidsActivity.this, CreateEventActivity.class), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //Refresh Screen
            loadAvailableRaids();
        }
    }

    @Override
    public void onRefresh() {
        loadAvailableRaids();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadAvailableRaids() {
        new AvailableRaidsTask(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                availableRaids = new ArrayList<>();
                try {
                    availableRaidsArray = new JSONArray(response);
                    for (int i = 0; i < availableRaidsArray.length(); i++) {
                        JSONObject tempAvailableRaid = availableRaidsArray.getJSONObject(i);
                        int id = tempAvailableRaid.getInt("id");
                        String title = tempAvailableRaid.getString("name");
                        String description = tempAvailableRaid.getString("description");
                        String raidlead = tempAvailableRaid.getString("raidlead");
                        String startsAt = tempAvailableRaid.getString("startdate");
                        String endsAt = tempAvailableRaid.getString("enddate");

                        availableRaids.add(new Raid(id, title, description, raidlead, startsAt, endsAt));
                    }
                    // specify an adapter (see also next example)
                    mAdapter = new AvailableRaidsAdapter(availableRaids);
                    mAdapter.setOnItemClickListener(new AvailableRaidsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            startActivity(new Intent(AvailableRaidsActivity.this, RaidsDetailActivity.class)
                                    .putExtra("raid_id", String.valueOf(availableRaids.get(position).id)));
                        }
                    });
                    availableRaidsView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AvailableRaidsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
        mDrawerLayout.closeDrawers();

        switch (position) {
            case 0:
                //Do nothing we are already in that activity
                return;
            case 1:
                Intent i = new Intent(AvailableRaidsActivity.this, CharacterActivity.class);
                startActivity(i);
                return;
            case 2:
                Intent j = new Intent(AvailableRaidsActivity.this, ProfileActivity.class);
                startActivity(j);
                return;
        }
    }
}
