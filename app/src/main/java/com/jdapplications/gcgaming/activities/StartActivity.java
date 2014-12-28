package com.jdapplications.gcgaming.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
        Toast.makeText(StartActivity.this, response, Toast.LENGTH_SHORT).show();
        loginSubmitButton.setEnabled(true);
        if (response != null) {
            try {
                jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    //TODO: Save Login and Password in SharedPreferences here
                    Toast.makeText(StartActivity.this, "Welcome, " + userLoginName.getText().toString(), Toast.LENGTH_SHORT).show();
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
