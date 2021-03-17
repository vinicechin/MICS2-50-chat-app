package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mics2_50.chatproject.adapter.MessageAdapter;
import com.mics2_50.chatproject.model.Message;

import java.util.logging.Logger;

public class ChatActivity extends AppCompatActivity {
    private EditText editTextMessage;
    private String username;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        // Get user name set in MainActivity
        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.USER_NAME);
    }

    public void sendMessage(final View view) {
        final String message = editTextMessage.getText().toString();
        if (message.length() > 0) {
            this.onMessage(message, this.username, true);
            // Should send the message to the network here
            editTextMessage.getText().clear();

            // simulates an answer
            Log.d("ChatActivity", message);
            if (message.equalsIgnoreCase("Hi")) {
                this.onMessage("Hi, how are you?", "Mock", false);
            }
        }
    }

    public void onMessage(String text, String username, boolean fromUser) {
        final Message message = new Message(text, username, fromUser);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.add(message);
                // scroll the ListView to the last added element
                messagesView.setSelection(messagesView.getCount() - 1);
            }
        });
    }
}