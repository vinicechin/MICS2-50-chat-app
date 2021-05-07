package com.mics2_50.chatproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mics2_50.chatproject.R;
import com.mics2_50.chatproject.model.Message;

import java.util.ArrayList;
import java.util.List;

public class PeerAdapter extends BaseAdapter {
    List<String> peers = new ArrayList<String>();
    List<Integer> avatarIds = new ArrayList<Integer>();
    Context context;

    public PeerAdapter(Context context) {
        this.context = context;
    }

    public void add(String username, Integer avatarId) {
        this.peers.add(username);
        this.avatarIds.add(avatarId);
        notifyDataSetChanged();
    }

    public void addAll(String[] usernames, Integer[] avatarIds) {
        for (int i=0; i<usernames.length; i++) {
            this.add(usernames[i], avatarIds[i]);
        }
    }

    public void clear() {
        this.peers.clear();
        this.avatarIds.clear();
    }

    @Override
    public int getCount() {
        return peers.size();
    }

    @Override
    public Object getItem(int position) {
        return peers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PeerViewHolder holder = new PeerViewHolder();
        LayoutInflater peerInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        String username = peers.get(position);
        Integer avatarId = avatarIds.get(position);

        // create peer
        convertView = peerInflater.inflate(R.layout.fragment_peer, null);
        holder.username = (TextView) convertView.findViewById(R.id.textView);
        holder.avatar = (ImageView)  convertView.findViewById(R.id.imgIcon);
        convertView.setTag(holder);

        holder.username.setText(username);
        if (avatarId != null) {
            holder.avatar.setImageResource(avatarId);
        }

        return convertView;
    }
}

class PeerViewHolder {
    public ImageView avatar;
    public TextView username;
}