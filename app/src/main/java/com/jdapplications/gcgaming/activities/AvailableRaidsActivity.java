package com.jdapplications.gcgaming.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.adapters.AvailableRaidsAdapter;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.models.Raid;
import com.jdapplications.gcgaming.tasks.AvailableRaidsTask;
import com.jdapplications.gcgaming.tasks.GCMSendRegistrationIDToServer;
import com.jdapplications.gcgaming.ui.FloatingActionButton;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private SharedPreferences sharedPref;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    String SENDER_ID = "375079589391";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM";
    GoogleCloudMessaging gcm;

    String regid;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_raids);

        sharedPref = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

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

        //DISMISS the notification if any are available
        NotificationManager nm = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
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

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId() {
        final SharedPreferences prefs = sharedPref;
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        new GCMSendRegistrationIDToServer(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                try {
                    JSONObject gcmRegRequest = new JSONObject(response);
                    if (gcmRegRequest.getInt("code") == 0) {
                        Log.i(TAG, "Registration ID was saved");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "Registration ID couldn't be saved");
            }
        }).execute(String.valueOf(sharedPref.getInt("id", 0)), regid);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = sharedPref;
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
