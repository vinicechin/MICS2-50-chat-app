package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME = "com.mics2_50.chatproject.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Enter button */
    public void sendMessage(View view) {
        // Initialize intent
        Intent intent = new Intent(this, ChatActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextUserName);
        String message = editText.getText().toString();
        intent.putExtra(USER_NAME, message);

        // Go to the ChatActivity
        startActivity(intent);
    }

}