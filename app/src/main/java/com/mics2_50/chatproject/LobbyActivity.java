package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mics2_50.chatproject.wifidirect.WifiDirectController;

public class LobbyActivity extends AppCompatActivity {
    private final String TAG = "APP-Lobby-Act";

    private ListView peersListView;
    private WifiDirectController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        controller = new WifiDirectController(this);

        setUserName();
        setPeersList();

        controller.leaveGroups();
        controller.discoverPeers();
    }

    private void setUserName() {
        // Get user name set in MainActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.USER_NAME);

        TextView loggedInTextView = findViewById(R.id.loggedIn);
        loggedInTextView.setText(String.format("You are logged in as %s", username));

        controller.setDeviceName(username);
    }

    private void setPeersList() {
        peersListView = findViewById(R.id.peersListView);
        TextView emptyText = findViewById(android.R.id.empty);
        peersListView.setEmptyView(emptyText);
        peersListView = findViewById(R.id.peersListView);
        peersListView.setAdapter(controller.getPeersAdapter());
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

}