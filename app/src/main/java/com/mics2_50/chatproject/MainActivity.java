package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import static com.mics2_50.chatproject.wifidirect.WifiDirectController.PEER_NAME;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String USER_NAME = "com.mics2_50.chatproject.USERNAME";
    public static final String LATITUDE = "com.mics2_50.chatproject.LATITUDE";
    public static final String LONGITUDE = "com.mics2_50.chatproject.LONGITUDE";
    public static final String PREFERENCES_NAME = "com.mics2_50.chatproject.dataStorage";

    private SharedPreferences sharedPref;
    private GoogleMap map;

    private EditText editText;
    private Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        // Set username if already stored
        editText = findViewById(R.id.editTextUserName);
        editText.setText(sharedPref.getString(USER_NAME, ""));

        // Disable button if no username typed yet
        enterButton = findViewById(R.id.enter_button);
        if (editText.getText().toString().equals("")) {
            enterButton.setEnabled(false);
        }

        // Add text change listener to username field
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

        // Add key pressed listener to username field
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                enterPressed(v);
                return true;
            }
            return false;
        });

        // Initial verification of all permissions
        checkPermission();
    }

    /** Called when the user taps the Enter button */
    public void enterPressed(View view) {
        // Initialize intent
        Intent intent = new Intent(this, LobbyActivity.class);
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.putExtra(PEER_NAME, "Mock");
        String username = editText.getText().toString();
        intent.putExtra(USER_NAME, username);

        // Save username on data storage
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_NAME, username);

        // Go to the ChatActivity after commit of username to storage
        if (editor.commit()) {
            startActivity(intent);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        enableMyLocation();

        Location loc = map.getMyLocation();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LATITUDE, "" + loc.getLatitude());
        editor.putString(LONGITUDE, "" + loc.getLongitude());
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }
}