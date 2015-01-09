package com.jdapplications.gcgaming.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.jdapplications.gcgaming.models.*;
import com.jdapplications.gcgaming.models.Character;
import com.jdapplications.gcgaming.tasks.BlizzClassTask;
import com.jdapplications.gcgaming.tasks.BlizzRaceTask;
import com.jdapplications.gcgaming.tasks.CreateCharacterTask;
import com.jdapplications.gcgaming.tasks.LoadCharacterDetailsTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddCharacterActivity extends ActionBarActivity implements View.OnClickListener {

    private Button submitButton;
    private Button saveCharButton;
    private EditText charServer;
    private EditText charName;
    private ImageView charAvatar;
    private TextView charNameTv;
    private TextView charClassTv;
    private TextView charRaceTv;
    private TextView charLevelTv;
    private TextView charItemlvl;
    private Character newCharacter;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_character);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        submitButton = (Button) findViewById(R.id.button_load_char);
        submitButton.setOnClickListener(this);
        saveCharButton = (Button) findViewById(R.id.button_save_char);
        saveCharButton.setOnClickListener(this);

        charServer = (EditText) findViewById(R.id.character_server);
        charName = (EditText) findViewById(R.id.character_name);

        charAvatar = (ImageView) findViewById(R.id.character_avatar);
        charNameTv = (TextView) findViewById(R.id.character_name_tv);
        charClassTv = (TextView) findViewById(R.id.character_class);
        charRaceTv = (TextView) findViewById(R.id.character_race);
        charLevelTv = (TextView) findViewById(R.id.character_level);
        charItemlvl = (TextView) findViewById(R.id.character_itemlvl);

        prefs = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_character, menu);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_load_char) {
            new LoadCharacterDetailsTask(new OnAsyncResultListener() {
                @Override
                public void onResult(String response) {
                    try {
                        JSONObject jsonChar = new JSONObject(response);
                        int lastModified = jsonChar.getInt("lastModified");
                        String realm = jsonChar.getString("realm");
                        String battleGroup = jsonChar.getString("battlegroup");
                        int achievementPoints = jsonChar.getInt("achievementPoints");
                        int gender = jsonChar.getInt("gender");
                        String charName = jsonChar.getString("name");
                        charNameTv.setText(charName);
                        String thumbnail = jsonChar.getString("thumbnail");
                        String thumbnailUrl = "http://eu.battle.net/static-render/eu/" + thumbnail;
                        Picasso.with(AddCharacterActivity.this).load(thumbnailUrl).into(charAvatar);
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

                        JSONObject items = jsonChar.getJSONObject("items");
                        int itemlvlEquipped = items.getInt("averageItemLevelEquipped");
                        int itemlvlTotal = items.getInt("averageItemLevel");
                        charItemlvl.setText(String.valueOf(itemlvlEquipped) + "( " + itemlvlTotal + " )");
                        newCharacter = new Character(lastModified, charName,
                                realm, battleGroup, charClass, race, gender,
                                level, achievementPoints, thumbnailUrl, itemlvlTotal,
                                itemlvlEquipped, prefs.getInt("id", 0));
                        saveCharButton.setEnabled(true);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        saveCharButton.setEnabled(false);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AddCharacterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    saveCharButton.setEnabled(false);
                    newCharacter = null;
                }
            }).execute(charServer.getText().toString(), charName.getText().toString());
        } else if (v.getId() == R.id.button_save_char) {
            if (newCharacter != null) {
                //Save character to server with user id
                new CreateCharacterTask(new OnAsyncResultListener() {
                    @Override
                    public void onResult(String response) {
                        try {
                            JSONObject jsonresp = new JSONObject(response);
                            if (jsonresp.getInt("code") == 0) {
                                Toast.makeText(AddCharacterActivity.this,
                                        "Character was saved!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AddCharacterActivity.this,
                                "Character couldn't be added. Try again or enter valid character name. "
                                        + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, newCharacter).execute();
            } else {
                Toast.makeText(AddCharacterActivity.this,
                        "Something is wrong. Please try to search for your character again "
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }
}
