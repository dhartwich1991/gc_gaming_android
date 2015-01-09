package com.jdapplications.gcgaming.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.models.Character;
import com.jdapplications.gcgaming.ui.RoundImageView;
import com.jdapplications.gcgaming.utils.BlizzRaces;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.jdapplications.gcgaming.utils.BlizzClasses.*;

/**
 * Created by danielhartwich on 1/8/15.
 */
public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListAdapter.ViewHolder> {

    private ArrayList<Character> characters;
    private Context ctx;

    OnItemClickListener mItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView charName, charServer, charRace, charClass, charLevel, charItemlvlEquipped, charItemlvlTotal, charAchievementPoints;
        public RoundImageView charImage;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            charName = (TextView) itemLayoutView.findViewById(R.id.char_name);
            charServer = (TextView) itemLayoutView.findViewById(R.id.char_server);
            charRace = (TextView) itemLayoutView.findViewById(R.id.char_race);
            charClass = (TextView) itemLayoutView.findViewById(R.id.char_class);
            charLevel = (TextView) itemLayoutView.findViewById(R.id.char_level);
            charItemlvlEquipped = (TextView) itemLayoutView.findViewById(R.id.char_itemlvl_equipped);
            charItemlvlTotal = (TextView) itemLayoutView.findViewById(R.id.char_itemlvl_total);
            charAchievementPoints = (TextView) itemLayoutView.findViewById(R.id.char_achievement_points);

            charImage = (RoundImageView) itemLayoutView.findViewById(R.id.character_image);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public CharacterListAdapter(ArrayList<Character> characters, Context ctx) {
        this.characters = characters;
        this.ctx = ctx;
    }


    @Override
    public CharacterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_my_characters, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CharacterListAdapter.ViewHolder viewHolder, int i) {
        final Character tempChar = characters.get(i);
        viewHolder.charName.setText(tempChar.name);
        viewHolder.charServer.setText(tempChar.realm);
        Picasso.with(ctx).load(tempChar.thumbNailUrl).into(viewHolder.charImage);

        switch (tempChar.race) {
            case BlizzRaces.HUMAN:
                viewHolder.charRace.setText("Human");
                break;
            case BlizzRaces.NIGHT_ELF:
                viewHolder.charRace.setText("Night elf");
                break;
            case BlizzRaces.DWARF:
                viewHolder.charRace.setText("Dwarf");
                break;
            case BlizzRaces.GNOME:
                viewHolder.charRace.setText("GNOME");
                break;
            case BlizzRaces.WORGEN:
                viewHolder.charRace.setText("Worgen");
                break;
            case BlizzRaces.DRAENEI:
                viewHolder.charRace.setText("Draenei");
                break;

            case BlizzRaces.ORC:
                viewHolder.charRace.setText("Orc");
                break;
            case BlizzRaces.TROLL:
                viewHolder.charRace.setText("Troll");
                break;
            case BlizzRaces.TAUREN:
                viewHolder.charRace.setText("Tauren");
                break;
            case BlizzRaces.BLOOD_ELF:
                viewHolder.charRace.setText("Blood elf");
                break;
            case BlizzRaces.GOBLIN:
                viewHolder.charRace.setText("Goblin");
                break;
            case BlizzRaces.UNDEAD:
                viewHolder.charRace.setText("Undead");
                break;

            case BlizzRaces.PANDAREN:
                viewHolder.charRace.setText("Pandaren");
                break;
            case BlizzRaces.PANDAREN_ALLI:
                viewHolder.charRace.setText("Pandaren (A)");
                break;
            case BlizzRaces.PANDAREN_HORDE:
                viewHolder.charRace.setText("Pandaren (H)");
                break;
        }

        switch (tempChar.charClass) {
            case HUNTER:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.hunter_green));
                viewHolder.charClass.setText("Hunter");
                break;
            case ROGUE:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.rogue_light_yellow));
                viewHolder.charClass.setText("Rogue");
                break;
            case WARRIOR:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.warrior_tan));
                viewHolder.charClass.setText("Warrior");
                break;
            case PALADIN:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.paladin_pink));
                viewHolder.charClass.setText("Paladin");
                break;
            case SHAMAN:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.shaman_blue));
                viewHolder.charClass.setText("Shaman");
                break;
            case MAGE:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.mage_light_blue));
                viewHolder.charClass.setText("Mage");
                break;
            case PRIEST:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.priest_white));
                viewHolder.charClass.setText("Priest");
                break;
            case DEATH_KNIGHT:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.death_knight_red));
                viewHolder.charClass.setText("Death Knight");
                break;
            case DRUID:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.druid_orange));
                viewHolder.charClass.setText("Druid");
                break;
            case WARLOCK:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.warlock_purple));
                viewHolder.charClass.setText("Warlock");
                break;
            case MONK:
                viewHolder.charClass.setTextColor(ctx.getResources().getColor(R.color.monk_jade_green));
                viewHolder.charClass.setText("Monk");
        }

        viewHolder.charLevel.setText(String.valueOf(tempChar.level));
        viewHolder.charItemlvlEquipped.setText(String.valueOf(tempChar.itemLevelEquipped));
        viewHolder.charItemlvlTotal.setText(String.valueOf(tempChar.itemLevelTotal));
        viewHolder.charAchievementPoints.setText(String.valueOf(tempChar.achievementPoints));
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }
}

