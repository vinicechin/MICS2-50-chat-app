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

public class MessageAdapter extends BaseAdapter {
    List<Message> messages = new ArrayList<Message>();
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(position);

        if (message.isFromUser()) {
            // create user message
            convertView = messageInflater.inflate(R.layout.chat_message_user, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else {
            // create received message
            convertView = messageInflater.inflate(R.layout.chat_message_received, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder.avatar = (ImageView)  convertView.findViewById(R.id.imgIcon);
            convertView.setTag(holder);

            holder.name.setText(message.getUsername());
            holder.messageBody.setText(message.getText());
            holder.avatar.setImageResource(message.getAvatarId());
        }

        return convertView;
    }
}

class MessageViewHolder {
    public ImageView avatar;
    public TextView name;
    public TextView messageBody;
}
