package com.jdapplications.gcgaming.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.models.Raid;
import com.jdapplications.gcgaming.utils.DateFormatter;

import java.util.ArrayList;

/**
 * Created by danielhartwich on 12/19/14.
 */
public class AvailableRaidsAdapter extends RecyclerView.Adapter<AvailableRaidsAdapter.ViewHolder> {

    private ArrayList<Raid> availableRaids;

    OnItemClickListener mItemClickListener;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView raidTitle, raidDescription, raidStart, raidEnd, raidLeads;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            raidTitle = (TextView) itemLayoutView.findViewById(R.id.raid_title);
            raidDescription = (TextView) itemLayoutView.findViewById(R.id.raid_desc);
            raidStart = (TextView) itemLayoutView.findViewById(R.id.raid_start);
            raidEnd = (TextView) itemLayoutView.findViewById(R.id.raid_end);
            raidLeads = (TextView) itemLayoutView.findViewById(R.id.raid_leaders);
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

    public AvailableRaidsAdapter(ArrayList<Raid> availableRaids) {
        this.availableRaids = availableRaids;
    }

    @Override
    public AvailableRaidsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_available_raids, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AvailableRaidsAdapter.ViewHolder viewHolder, int i) {
        Raid tempRaid = availableRaids.get(i);
        viewHolder.raidTitle.setText(tempRaid.name);
        viewHolder.raidDescription.setText(tempRaid.description);

        viewHolder.raidStart.setText(DateFormatter.formatJSONISO8601Date(tempRaid.startsAt));
        viewHolder.raidEnd.setText(DateFormatter.formatJSONISO8601Date(tempRaid.endsAt));
        viewHolder.raidLeads.setText(tempRaid.leader);
    }

    @Override
    public int getItemCount() {
        return availableRaids.size();
    }
}
