package com.jdapplications.gcgaming.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.adapters.CharacterListAdapter;
import com.jdapplications.gcgaming.adapters.RaidMemberListAdapter;
import com.jdapplications.gcgaming.listener.OnAsyncResultListener;
import com.jdapplications.gcgaming.models.*;
import com.jdapplications.gcgaming.models.Character;
import com.jdapplications.gcgaming.tasks.IsSignedUpTask;
import com.jdapplications.gcgaming.tasks.MyCharactersTask;
import com.jdapplications.gcgaming.tasks.RaidDetailTask;
import com.jdapplications.gcgaming.tasks.SignOffForRaidTask;
import com.jdapplications.gcgaming.tasks.SignUpForRaidTask;
import com.jdapplications.gcgaming.ui.DividerItemDecoration;
import com.jdapplications.gcgaming.ui.FloatingActionButton;
import com.jdapplications.gcgaming.ui.RoundImageView;
import com.jdapplications.gcgaming.utils.DateFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.jdapplications.gcgaming.utils.BlizzClasses.DEATH_KNIGHT;
import static com.jdapplications.gcgaming.utils.BlizzClasses.DRUID;
import static com.jdapplications.gcgaming.utils.BlizzClasses.HUNTER;
import static com.jdapplications.gcgaming.utils.BlizzClasses.MAGE;
import static com.jdapplications.gcgaming.utils.BlizzClasses.MONK;
import static com.jdapplications.gcgaming.utils.BlizzClasses.PALADIN;
import static com.jdapplications.gcgaming.utils.BlizzClasses.PRIEST;
import static com.jdapplications.gcgaming.utils.BlizzClasses.ROGUE;
import static com.jdapplications.gcgaming.utils.BlizzClasses.SHAMAN;
import static com.jdapplications.gcgaming.utils.BlizzClasses.WARLOCK;
import static com.jdapplications.gcgaming.utils.BlizzClasses.WARRIOR;

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

    private LinearLayout raidersList;

    public ArrayList<Character> chars;
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

        raidersList = (LinearLayout) findViewById(R.id.linear_full_list);

        mLayoutManager = new LinearLayoutManager(this);
        raidMembers = (RecyclerView) findViewById(R.id.raid_members);
        raidMembers.setLayoutManager(mLayoutManager);
        raidMembers.setItemAnimator(new DefaultItemAnimator());
        raidMembers.addItemDecoration(new DividerItemDecoration(RaidsDetailActivity.this, null));

        fab = (FloatingActionButton) findViewById(R.id.fab_sign_up);
        fab.setOnCheckedChangeListener(this);
        sharedPref = getApplicationContext().getSharedPreferences("com.jdapplications.gcgaming", Context.MODE_PRIVATE);

        chars = new ArrayList<>();

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

    private void signUpForRaid(final String raidId, String userId, String characterId, String role) {
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
        }).execute(raidId, userId, characterId, role);
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
            raidDescription.setText("Description: " + description);
            raidLead.setText("Raidlead: " + raidlead);
            raidStart.setText("Start: " + DateFormatter.formatJSONISO8601Date(start));
            raidEnd.setText("End: " + DateFormatter.formatJSONISO8601Date(end));

            //Load Raid Members
            raidMembersList = new ArrayList<>();
            JSONArray members = jsonResponse.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject tempMember = members.getJSONObject(i);
                int id = tempMember.getInt("id");
                int charid = tempMember.getInt("charid");
                String username = tempMember.getString("username");
                String character = tempMember.getString("charname");
                String realm = tempMember.getString("realm");
                String role = tempMember.getString("role");
                int race = tempMember.getInt("race");
                int characterClass = tempMember.getInt("character_class");
                String thumbnailurl = tempMember.getString("thumbnail");
                int level = tempMember.getInt("level");
                int itemlvlequipped = tempMember.getInt("itemlvlequipped");
                int itemlvltotal = tempMember.getInt("itemlvltotal");
                raidMembersList.add(new User(id, username,
                        new Character(charid, character, realm, race, characterClass,
                                thumbnailurl, level, itemlvlequipped, itemlvltotal, role)));
                //Do stuff with member

            }
            RaidMemberListAdapter adapter = new RaidMemberListAdapter(raidMembersList, RaidsDetailActivity.this);
            raidMembers.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            raidersList.removeAllViews();
            for (int i = 0; i < raidMembersList.size(); i++) {
                /**
                 * inflate items/ add items in linear layout instead of listview
                 */
                LayoutInflater inflater = null;
                inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mLinearView = inflater.inflate(R.layout.entry_raid_details_members, null);

                View mSeperatorView = inflater.inflate(R.layout.seperator_list, null);
                /**
                 * getting id of row.xml
                 */
                TextView characterUser = (TextView) mLinearView.findViewById(R.id.character_user);
                ImageView memberStatus = (ImageView) mLinearView.findViewById(R.id.invitation_status);
                RoundImageView characterThumbnail = (RoundImageView) mLinearView.findViewById(R.id.character_image);
                ImageView characterRole = (ImageView) mLinearView.findViewById(R.id.character_spec);
                TextView characterName = (TextView) mLinearView.findViewById(R.id.character_name);
                TextView characterServer = (TextView) mLinearView.findViewById(R.id.character_server);
                TextView charClass = (TextView) mLinearView.findViewById(R.id.character_class);
                TextView characterGearscore = (TextView) mLinearView.findViewById(R.id.character_gearscore);

                User tempUser = raidMembersList.get(i);
                Character tempChar = tempUser.character;
                Picasso.with(RaidsDetailActivity.this).load(tempChar.thumbNailUrl).into(characterThumbnail);
                characterUser.setText("by " + tempUser.username);
                characterName.setText(tempChar.name);
                characterServer.setText(tempChar.realm);

                switch (tempChar.role) {
                    case "Tank":
                        characterRole.setImageDrawable(RaidsDetailActivity.this.getResources().getDrawable(R.drawable.tank_icon));
                        break;
                    case "Heal":
                        characterRole.setImageDrawable(RaidsDetailActivity.this.getResources().getDrawable(R.drawable.heal_icon));
                        break;
                    case "Damage":
                        characterRole.setImageDrawable(RaidsDetailActivity.this.getResources().getDrawable(R.drawable.damage_icon));
                        break;
                }
                String gearscore = tempChar.itemLevelEquipped + " ( " + tempChar.itemLevelTotal + " )";
                characterGearscore.setText(gearscore);

                switch (tempChar.charClass) {
                    case HUNTER:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.hunter_green));
                        charClass.setText("Hunter");
                        break;
                    case ROGUE:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.rogue_light_yellow));
                        charClass.setText("Rogue");
                        break;
                    case WARRIOR:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.warrior_tan));
                        charClass.setText("Warrior");
                        break;
                    case PALADIN:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.paladin_pink));
                        charClass.setText("Paladin");
                        break;
                    case SHAMAN:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.shaman_blue));
                        charClass.setText("Shaman");
                        break;
                    case MAGE:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.mage_light_blue));
                        charClass.setText("Mage");
                        break;
                    case PRIEST:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.priest_white));
                        charClass.setText("Priest");
                        break;
                    case DEATH_KNIGHT:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.death_knight_red));
                        charClass.setText("Death Knight");
                        break;
                    case DRUID:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.druid_orange));
                        charClass.setText("Druid");
                        break;
                    case WARLOCK:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.warlock_purple));
                        charClass.setText("Warlock");
                        break;
                    case MONK:
                        charClass.setTextColor(RaidsDetailActivity.this.getResources().getColor(R.color.monk_jade_green));
                        charClass.setText("Monk");
                }
                raidersList.addView(mLinearView);
                if (i < raidMembersList.size()-1) {
                    raidersList.addView(mSeperatorView);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



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
                chars = new ArrayList<>();
                loadCharactersAndShowDialog();
            }
        } else {
            Log.d("Check changed", "false");
            if (isSignedUp) {
                signOffForRaid(raidId, String.valueOf(sharedPref.getInt("id", 0)));
            }
        }
    }

    private void loadCharactersAndShowDialog() {
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

                        Character myChar = new Character(id, lastModified, charName, realm,
                                battleGroup, charClass, race, gender, level,
                                achievementPoints, thumbnail, itemlvltotal,
                                itemlvlequipped, userid);
                        chars.add(myChar);
                    }

                    final AlertDialog alertDialog = new myCustomAlertDialog(RaidsDetailActivity.this, chars);
                    alertDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {

            }
        }).execute(String.valueOf(sharedPref.getInt("id", 0)));


    }

    public class myCustomAlertDialog extends AlertDialog {

        protected myCustomAlertDialog(Context context, ArrayList<Character> chars) {
            super(context);

            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.dialog_select_character, null);
            setView(convertView);
            setTitle("List");
            RecyclerView rv = (RecyclerView) convertView.findViewById(R.id.dialog_character_list);
            RecyclerView.LayoutManager dialogLayoutManager = new LinearLayoutManager(context);
            rv.setLayoutManager(dialogLayoutManager);
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.addItemDecoration(new DividerItemDecoration(context, null));
            CharacterListAdapter dialogAdapter = new CharacterListAdapter(chars, context);
            final ArrayList<Character> charas = chars;
            dialogAdapter.setOnItemClickListener(new CharacterListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //Select Character and store in db
                    //dismiss dialog afterwards
                    //Sign up for the raid
                    showRoleDialog(raidId, String.valueOf(sharedPref.getInt("id", 0)), String.valueOf(charas.get(position).id));
                    dismiss();
                    fab.setChecked(false);
                }
            });
            rv.setAdapter(dialogAdapter);
            setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                    fab.setChecked(false);
                }
            });

        }

        private void showRoleDialog(final String raidId, final String userId, final String characterId) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                    RaidsDetailActivity.this);
            builderSingle.setTitle("Select your Role");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    RaidsDetailActivity.this,
                    android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Tank");
            arrayAdapter.add("Heal");
            arrayAdapter.add("Damage");
            builderSingle.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            fab.setChecked(false);
                        }
                    });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String role = arrayAdapter.getItem(which);
                            Log.d("Role", role);
                            signUpForRaid(raidId, userId, characterId, role);
                        }
                    });
            builderSingle.show();
        }
    }
}
