package com.jdapplications.gcgaming.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.tasks.UserProfileTask;
import com.jdapplications.gcgaming.utils.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity {

    private TextView userName;
    private TextView userEmail;
    private TextView userAdmin;
    private TextView userModerator;
    private TextView userToken;
    private TextView userCreatedAt;
    private TextView userUpdatedAt;
    private TextView userGcmReg;
    private TextView userAppVersion;
    private TextView userId;

    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        sharedPrefs = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);

        userName = (TextView) findViewById(R.id.profile_username);
        userEmail = (TextView) findViewById(R.id.profile_email);
        userAdmin = (TextView) findViewById(R.id.profile_admin);
        userModerator = (TextView) findViewById(R.id.profile_moderator);
        userToken = (TextView) findViewById(R.id.profile_access_token);
        userCreatedAt = (TextView) findViewById(R.id.profile_created_at);
        userUpdatedAt = (TextView) findViewById(R.id.profile_updated_at);
        userGcmReg = (TextView) findViewById(R.id.profile_gcm_id);
        userAppVersion = (TextView) findViewById(R.id.profile_app_version);
        userId = (TextView) findViewById(R.id.profile_user_id);

        requestUserProfile();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestUserProfile() {
        new UserProfileTask(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    int id = jsonObj.getInt("id");
                    String username = jsonObj.getString("username");
                    String email = jsonObj.getString("email");
                    boolean admin = jsonObj.optBoolean("admin", false);
                    boolean moderator = jsonObj.optBoolean("moderator", false);
                    String createdAt = jsonObj.getString("created_at");
                    String updatedAt = jsonObj.getString("updated_at");
                    String accessToken = jsonObj.getString("access_token");
                    String gcmRegId = jsonObj.getString("gcm_reg_id");
                    PackageInfo packageInfo = ProfileActivity.this.getPackageManager()
                            .getPackageInfo(ProfileActivity.this.getPackageName(), 0);
                    int appVersion = packageInfo.versionCode;

                    userId.setText(String.valueOf(id));
                    userName.setText(username);
                    userEmail.setText(email);
                    userAdmin.setText(String.valueOf(admin));
                    userModerator.setText(String.valueOf(moderator));
                    userCreatedAt.setText(DateFormatter.formatJSONISO8601Date(createdAt));
                    userUpdatedAt.setText(DateFormatter.formatJSONISO8601Date(updatedAt));
                    userToken.setText(accessToken);
                    userGcmReg.setText(gcmRegId);
                    userAppVersion.setText(String.valueOf(appVersion));

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this, "Profile couldn't be loaded", Toast.LENGTH_SHORT).show();
            }
        }).execute(String.valueOf(sharedPrefs.getInt("id", 0)));
    }
}
