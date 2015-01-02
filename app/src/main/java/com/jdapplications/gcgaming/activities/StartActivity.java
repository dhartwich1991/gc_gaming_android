package com.jdapplications.gcgaming.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.tasks.LoginTask;
import com.jdapplications.gcgaming.tasks.RegisterTask;

import org.json.JSONException;
import org.json.JSONObject;

public class StartActivity extends ActionBarActivity implements View.OnClickListener, OnAsyncResultListener {
    private TextView registerTextView;
    private Button loginSubmitButton;
    private LoginTask task;
    private OnAsyncResultListener asyncListener;
    private EditText userLoginName, userLoginPassword;
    private JSONObject jsonResponse;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().setTitle("Login");
        registerTextView = (TextView) findViewById(R.id.registerAsNew);
        registerTextView.setOnClickListener(this);
        loginSubmitButton = (Button) findViewById(R.id.buttonLogin);
        loginSubmitButton.setOnClickListener(this);
        userLoginName = (EditText) findViewById(R.id.userLoginName);
        userLoginPassword = (EditText) findViewById(R.id.userLoginPassword);
        setOnAsyncResultListener(this);
        sharedPref = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (sharedPref.getString("access_token", null) != null) {
            task = new LoginTask(asyncListener);
            task.execute(sharedPref.getString("username", null), sharedPref.getString("password", null));
            loginSubmitButton.setEnabled(false);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerAsNew) {
            startActivity(new Intent(StartActivity.this, RegisterActivity.class));
        } else if (v.getId() == R.id.buttonLogin) {
            task = new LoginTask(asyncListener);
            task.execute(userLoginName.getText().toString(), userLoginPassword.getText().toString());
            loginSubmitButton.setEnabled(false);
        }
    }

    void setOnAsyncResultListener(OnAsyncResultListener listener) {
        this.asyncListener = listener;
    }

    @Override
    public void onResult(String response) {
        loginSubmitButton.setEnabled(true);
        if (response != null) {
            try {
                jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONObject loginUser = jsonResponse.optJSONObject("user");
                    editor.putBoolean("admin", loginUser.optBoolean("admin", false));
                    editor.putBoolean("moderator", loginUser.optBoolean("moderator", false));
                    editor.commit();
                    if (sharedPref.getString("access_token", null) == null) {
                        editor.putInt("id", loginUser.getInt("id"));
                        editor.putString("username", loginUser.getString("username"));
                        editor.putString("password", userLoginPassword.getText().toString());
                        editor.putString("access_token", loginUser.getString("access_token"));
                        editor.putString("email", loginUser.getString("email"));
                        editor.commit();
                    }
                    Toast.makeText(StartActivity.this, "Welcome, " + loginUser.getString("username"), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StartActivity.this, AvailableRaidsActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        loginSubmitButton.setEnabled(true);
    }
}
