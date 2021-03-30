package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mics2_50.chatproject.wifidirect.WifiDirectController;

import java.lang.reflect.Method;
import java.util.ArrayList;

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

        TextView loggedInTextView = (TextView) findViewById(R.id.loggedIn);
        loggedInTextView.setText(String.format("You are logged in as %s", username));

        controller.setDeviceName(username);
    }

    private void setPeersList() {
        peersListView = (ListView) findViewById(R.id.peersListView);
        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        peersListView.setEmptyView(emptyText);
        peersListView = (ListView) findViewById(R.id.peersListView);
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