package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get user name set in MainActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.USER_NAME);

        // Get text field and update its text
        TextView textView = findViewById(R.id.textViewUserName);
        textView.setText(username);
    }
}