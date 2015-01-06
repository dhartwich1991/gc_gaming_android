package com.jdapplications.gcgaming.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.tasks.BlizzClassTask;
import com.jdapplications.gcgaming.tasks.BlizzRaceTask;
import com.jdapplications.gcgaming.tasks.LoadCharacterDetailsTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CharacterActivity extends ActionBarActivity implements View.OnClickListener {

    private Button submitButton;
    private EditText charServer;
    private EditText charName;
    private ImageView charAvatar;
    private TextView charNameTv;
    private TextView charClassTv;
    private TextView charRaceTv;
    private TextView charLevelTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        submitButton = (Button) findViewById(R.id.button_load_char);
        submitButton.setOnClickListener(this);

        charServer = (EditText) findViewById(R.id.character_server);
        charName = (EditText) findViewById(R.id.character_name);

        charAvatar = (ImageView) findViewById(R.id.character_avatar);
        charNameTv = (TextView) findViewById(R.id.character_name_tv);
        charClassTv = (TextView) findViewById(R.id.character_class);
        charRaceTv = (TextView) findViewById(R.id.character_race);
        charLevelTv = (TextView) findViewById(R.id.character_level);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_character, menu);
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
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_load_char) {
            new LoadCharacterDetailsTask(new OnAsyncResultListener() {
                @Override
                public void onResult(String response) {
                    try {
                        JSONObject jsonChar = new JSONObject(response);
                        charNameTv.setText(jsonChar.getString("name"));
                        String thumbnail = jsonChar.getString("thumbnail");
                        String thumbnailUrl = "http://eu.battle.net/static-render/eu/" + thumbnail;
                        Picasso.with(CharacterActivity.this).load(thumbnailUrl).into(charAvatar);
                        final int charClass = jsonChar.getInt("class");
                        new BlizzClassTask(new OnAsyncResultListener() {
                            @Override
                            public void onResult(String response) {
                                try {
                                    JSONObject jsonClasses = new JSONObject(response);
                                    JSONArray classArray = jsonClasses.getJSONArray("classes");
                                    for (int i = 0; i < classArray.length(); i++) {
                                        JSONObject tempClass = classArray.getJSONObject(i);
                                        if (tempClass.getInt("id") == charClass) {
                                            charClassTv.setText(tempClass.getString("name"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        }).execute();

                        final int race = jsonChar.getInt("race");
                        new BlizzRaceTask(new OnAsyncResultListener() {
                            @Override
                            public void onResult(String response) {
                                try {
                                    JSONObject jsonRaces = new JSONObject(response);
                                    JSONArray raceArray = jsonRaces.getJSONArray("races");
                                    for (int i = 0; i < raceArray.length(); i++) {
                                        JSONObject tempRace = raceArray.getJSONObject(i);
                                        if (tempRace.getInt("id") == race) {
                                            charRaceTv.setText(tempRace.getString("name"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        }).execute();
                        int level = jsonChar.getInt("level");
                        charLevelTv.setText(String.valueOf(level));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(CharacterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).execute(charServer.getText().toString(), charName.getText().toString());
        }
    }
}
