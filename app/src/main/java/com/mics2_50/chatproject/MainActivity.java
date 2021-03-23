package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME = "com.mics2_50.chatproject.USERNAME";

    private EditText editText;
    private Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterButton = (Button) findViewById(R.id.enter_button);
        enterButton.setEnabled(false);

        editText = (EditText) findViewById(R.id.editTextUserName);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()==0) {
                    enterButton.setEnabled(false);
                } else if (!enterButton.isEnabled()) {
                    enterButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /** Called when the user taps the Enter button */
    public void enterPressed(View view) {
        // Initialize intent
//        Intent intent = new Intent(this, LobbyActivity.class);
        Intent intent = new Intent(this, ChatActivity.class);
        String username = editText.getText().toString();
        intent.putExtra(USER_NAME, username);

        // Go to the ChatActivity
        startActivity(intent);
    }

}