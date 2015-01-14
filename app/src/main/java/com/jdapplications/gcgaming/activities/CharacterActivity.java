package com.jdapplications.gcgaming.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.adapters.CharacterListAdapter;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.models.Character;
import com.jdapplications.gcgaming.tasks.BlizzClassTask;
import com.jdapplications.gcgaming.tasks.BlizzRaceTask;
import com.jdapplications.gcgaming.tasks.LoadCharacterDetailsTask;
import com.jdapplications.gcgaming.tasks.MyCharactersTask;
import com.jdapplications.gcgaming.tasks.UpdateCharacterTask;
import com.jdapplications.gcgaming.ui.DividerItemDecoration;
import com.jdapplications.gcgaming.ui.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CharacterActivity extends ActionBarActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FloatingActionButton fab;
    private RecyclerView yourCharacters;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences prefs;
    private CharacterListAdapter mAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<Character> myChars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        prefs = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);

        fab = (FloatingActionButton) findViewById(R.id.fab_add_character);
        fab.setOnClickListener(this);

        yourCharacters = (RecyclerView) findViewById(R.id.your_characters);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        yourCharacters.setLayoutManager(mLayoutManager);
        yourCharacters.setItemAnimator(new DefaultItemAnimator());
        yourCharacters.addItemDecoration(new DividerItemDecoration(CharacterActivity.this, null));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);


        loadCharacters();


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
        if (v.getId() == R.id.fab_add_character) {
            startActivityForResult(new Intent(CharacterActivity.this, AddCharacterActivity.class), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //TODO: Refresh page
            loadCharacters();
        }
    }

    private void loadCharacters() {
        myChars = new ArrayList<>();
        new MyCharactersTask(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonChars = new JSONObject(response);
                    JSONArray myCharacters = jsonChars.getJSONArray("characters");
                    for (int i = 0; i < myCharacters.length(); i++) {
                        JSONObject tempChar = myCharacters.getJSONObject(i);
                        int id = tempChar.getInt("id");
                        int lastModified = tempChar.getInt("lastModified");
                        String realm = tempChar.getString("realm");
                        String battleGroup = tempChar.getString("battlegroup");
                        int achievementPoints = tempChar.getInt("achievementPoints");
                        int gender = tempChar.getInt("gender");
                        String charName = tempChar.getString("name");
                        String thumbnail = tempChar.getString("thumbnailurl");

                        int charClass = tempChar.getInt("character_class");
                        int race = tempChar.getInt("race");
                        int level = tempChar.getInt("level");
                        int itemlvlequipped = tempChar.getInt("itemlevelequipped");
                        int itemlvltotal = tempChar.getInt("itemleveltotal");
                        int userid = tempChar.getInt("user_id");

                        myChars.add(new Character(id, lastModified, charName, realm,
                                battleGroup, charClass, race, gender, level,
                                achievementPoints, thumbnail, itemlvltotal,
                                itemlvlequipped, userid));
                    }
                    updateCharacters();
                    mAdapter = new CharacterListAdapter(myChars, CharacterActivity.this);
                    mAdapter.setOnItemClickListener(new CharacterListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Log.d("CharPosition", String.valueOf(position));
                        }
                    });
                    yourCharacters.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        }).execute(String.valueOf(prefs.getInt("id", 0)));
    }

    private void updateCharacters() {
        //TODO: Update Server Database with Records from Blizzard

        for (int i = 0; i < myChars.size(); i++) {
            final int finalI = i;
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
                        String thumbnail = jsonChar.getString("thumbnail");
                        String thumbnailUrl = "http://eu.battle.net/static-render/eu/" + thumbnail;
                        int charClass = jsonChar.getInt("class");
                        int race = jsonChar.getInt("race");
                        int level = jsonChar.getInt("level");
                        JSONObject items = jsonChar.getJSONObject("items");
                        int itemlvlEquipped = items.getInt("averageItemLevelEquipped");
                        int itemlvlTotal = items.getInt("averageItemLevel");

                        new UpdateCharacterTask(new OnAsyncResultListener() {
                            @Override
                            public void onResult(String response) {
                                try {
                                    JSONObject updateResult = new JSONObject(response);
                                    if (updateResult.getInt("code") == 0) {
                                        Log.d("Update successful", "update worked");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d("Update successful", "update failed");
                            }
                        }, new Character(myChars.get(finalI).id, lastModified, charName,
                                realm, battleGroup, charClass, race, gender, level,
                                achievementPoints, thumbnailUrl, itemlvlTotal, itemlvlEquipped,
                                myChars.get(finalI).userid)).execute();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                }
            }).execute(myChars.get(i).realm, myChars.get(i).name);
        }
    }

    @Override
    public void onRefresh() {
        loadCharactersWithoutUpdating();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadCharactersWithoutUpdating() {
        myChars = new ArrayList<>();
        new MyCharactersTask(new OnAsyncResultListener() {
            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonChars = new JSONObject(response);
                    JSONArray myCharacters = jsonChars.getJSONArray("characters");
                    for (int i = 0; i < myCharacters.length(); i++) {
                        JSONObject tempChar = myCharacters.getJSONObject(i);
                        int id = tempChar.getInt("id");
                        int lastModified = tempChar.getInt("lastModified");
                        String realm = tempChar.getString("realm");
                        String battleGroup = tempChar.getString("battlegroup");
                        int achievementPoints = tempChar.getInt("achievementPoints");
                        int gender = tempChar.getInt("gender");
                        String charName = tempChar.getString("name");
                        String thumbnail = tempChar.getString("thumbnailurl");

                        int charClass = tempChar.getInt("character_class");
                        int race = tempChar.getInt("race");
                        int level = tempChar.getInt("level");
                        int itemlvlequipped = tempChar.getInt("itemlevelequipped");
                        int itemlvltotal = tempChar.getInt("itemleveltotal");
                        int userid = tempChar.getInt("user_id");

                        myChars.add(new Character(id, lastModified, charName, realm,
                                battleGroup, charClass, race, gender, level,
                                achievementPoints, thumbnail, itemlvltotal,
                                itemlvlequipped, userid));
                    }
                    mAdapter = new CharacterListAdapter(myChars, CharacterActivity.this);
                    mAdapter.setOnItemClickListener(new CharacterListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Log.d("CharPosition", String.valueOf(position));
                        }
                    });
                    yourCharacters.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        }).execute(String.valueOf(prefs.getInt("id", 0)));
    }
}