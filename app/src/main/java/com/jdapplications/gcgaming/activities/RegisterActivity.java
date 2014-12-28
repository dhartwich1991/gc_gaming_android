package com.jdapplications.gcgaming.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.tasks.RegisterTask;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener, OnAsyncResultListener {
    private EditText usernameField, passwordField;
    private Button submitButton;
    private OnAsyncResultListener asyncListener;
    private RegisterTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        setOnAsyncResultListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
        if (v.getId() == R.id.submitButton) {
            task = new RegisterTask(asyncListener);
            task.execute(usernameField.getText().toString(), passwordField.getText().toString());
            submitButton.setEnabled(false);
            //  String jsonResult = asyntask.execute(usernameField.getText().toString(), passwordField.getText().toString());
        }
    }

    @Override
    public void onResult(String response) {
        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
        submitButton.setEnabled(true);
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        submitButton.setEnabled(true);
    }

    void setOnAsyncResultListener(OnAsyncResultListener listener) {
        this.asyncListener = listener;
    }
}

