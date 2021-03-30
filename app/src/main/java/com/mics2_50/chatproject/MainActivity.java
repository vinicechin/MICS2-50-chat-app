package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME = "com.mics2_50.chatproject.USERNAME";
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

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

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    enterPressed(v);
                    return true;
                }
                return false;
            }
        });

        checkPermission();
    }

    /** Called when the user taps the Enter button */
    public void enterPressed(View view) {
        // Initialize intent
        Intent intent = new Intent(this, LobbyActivity.class);
//        Intent intent = new Intent(this, ChatActivity.class);
        String username = editText.getText().toString();
        intent.putExtra(USER_NAME, username);

        // Go to the ChatActivity
        startActivity(intent);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            // if the permissions have already been granted do the following
            Log.d("permission", "already granted");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 0);
        } else {
            // if the permissions have already been granted do the following
            Log.d("permission", "already granted");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 0);
        } else {
            // if the permissions have already been granted do the following
            Log.d("permission", "already granted");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 0);
        } else {
            // if the permissions have already been granted do the following
            Log.d("permission", "already granted");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CHANGE_NETWORK_STATE}, 0);
        } else {
            // if the permissions have already been granted do the following
            Log.d("permission", "already granted");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
        } else {
            // if the permissions have already been granted do the following
            Log.d("permission", "already granted");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            // if the permissions have already been granted do the following
            Log.d("permission", "already granted");
        }
    }
}