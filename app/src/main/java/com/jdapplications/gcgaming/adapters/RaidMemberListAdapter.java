package com.jdapplications.gcgaming.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.models.*;
import com.jdapplications.gcgaming.models.Character;
import com.jdapplications.gcgaming.ui.RoundImageView;
import com.squareup.picasso.Picasso;

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

/**
 * Created by danielhartwich on 1/5/15.
 */
public class RaidMemberListAdapter extends RecyclerView.Adapter<RaidMemberListAdapter.ViewHolder> {
    private ArrayList<User> memberList;
    private Context ctx;
    OnItemClickListener mItemClickListener;

    public RaidMemberListAdapter(ArrayList<User> memberList, Context ctx) {
        this.memberList = memberList;
        this.ctx = ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView characterUser;
        public ImageView memberStatus;
        public ImageView characterRole;
        public RoundImageView characterThumbnail;
        public TextView characterName;
        public TextView characterServer;
        public TextView charClass;
        public TextView characterGearscore;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            characterUser = (TextView) itemLayoutView.findViewById(R.id.character_user);
            memberStatus = (ImageView) itemLayoutView.findViewById(R.id.invitation_status);
            characterThumbnail = (RoundImageView) itemLayoutView.findViewById(R.id.character_image);
            characterRole = (ImageView) itemLayoutView.findViewById(R.id.character_spec);
            characterName = (TextView) itemLayoutView.findViewById(R.id.character_name);
            characterServer = (TextView) itemLayoutView.findViewById(R.id.character_server);
            charClass = (TextView) itemLayoutView.findViewById(R.id.character_class);
            characterGearscore = (TextView) itemLayoutView.findViewById(R.id.character_gearscore);
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

    @Override
    public RaidMemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_raid_details_members, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RaidMemberListAdapter.ViewHolder viewHolder, int i) {
        User tempUser = memberList.get(i);
        Character tempChar = tempUser.character;
        Picasso.with(ctx).load(tempChar.thumbNailUrl).into(viewHolder.characterThumbnail);
        viewHolder.characterUser.setText("by "+tempUser.username);
        viewHolder.characterName.setText(tempChar.name);
        viewHolder.characterServer.setText(tempChar.realm);

        switch (tempChar.role) {
            case "Tank":
                viewHolder.characterRole.setImageDrawable(ctx.getResources().getDrawable(R.drawable.tank_icon));
                break;
            case "Heal":
                viewHolder.characterRole.setImageDrawable(ctx.getResources().getDrawable(R.drawable.heal_icon));
                break;
            case "Damage":
                viewHolder.characterRole.setImageDrawable(ctx.getResources().getDrawable(R.drawable.damage_icon));
                break;
        }
        String gearscore = tempChar.itemLevelEquipped + " ( " + tempChar.itemLevelTotal + " )";
        viewHolder.characterGearscore.setText(gearscore);

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
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
