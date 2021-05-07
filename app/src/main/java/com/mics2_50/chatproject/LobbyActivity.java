package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mics2_50.chatproject.wifidirect.WifiDirectController;

import static com.mics2_50.chatproject.MainActivity.PREFERENCES_NAME;

public class LobbyActivity extends AppCompatActivity {
    private final String TAG = "APP-Lobby-Act";

    private ListView peersListView;
    private WifiDirectController controller;
    private String username;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        setTitle("Available Chats");

        sharedPref = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        // Get user name set in MainActivity
        Intent intent = getIntent();

        String username = intent.getStringExtra(MainActivity.USER_NAME);
        if (username == null) {
            this.username = sharedPref.getString(MainActivity.USER_NAME, "");
        } else {
            this.username = username;
        }

        controller = new WifiDirectController(this, this.username);

        setUserName();
        setPeersList();

        controller.leaveGroups();
        controller.discoverPeers();
    }

    private void setUserName() {
        TextView loggedInTextView = findViewById(R.id.loggedIn);
        loggedInTextView.setText(String.format("Welcome %s!", this.username));

        controller.setDeviceName(username);
    }

    private void setPeersList() {
        peersListView = findViewById(R.id.peersListView);
        TextView emptyText = findViewById(android.R.id.empty);
        peersListView.setEmptyView(emptyText);
        peersListView = findViewById(R.id.peersListView);
        peersListView.setAdapter(controller.getPeersAdapter());
        peersListView.setOnItemClickListener((parent, view, position, id) -> {
            TextView item = view.findViewById(R.id.textView);
            String peername = item.getText().toString();

            Log.d(TAG, "Chat clicked: " + peername);
            controller.connectToPeer(position);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "on resume called");
        controller.registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "on pause called");
        controller.unregisterReceiver();
    }

    public void onRefresh(View view) {
        Log.d(TAG, "Refresh clicked");
        controller.discoverPeers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            controller.leaveGroups();
            controller.discoverPeers();
        }
    }

}