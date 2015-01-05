package com.jdapplications.gcgaming.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.models.User;

import java.util.ArrayList;

/**
 * Created by danielhartwich on 1/5/15.
 */
public class RaidMemberListAdapter extends RecyclerView.Adapter<RaidMemberListAdapter.ViewHolder> {
    private ArrayList<User> memberList;
    OnItemClickListener mItemClickListener;

    public RaidMemberListAdapter(ArrayList<User> memberList) {
        this.memberList = memberList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView memberName;
        public ImageView memberStatus;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            memberName = (TextView) itemLayoutView.findViewById(R.id.member_name);
            memberStatus = (ImageView) itemLayoutView.findViewById(R.id.invitation_status);
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
        viewHolder.memberName.setText(tempUser.username);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
