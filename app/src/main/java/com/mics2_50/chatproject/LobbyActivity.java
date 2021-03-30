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

import java.lang.reflect.Method;
import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {
    private final String TAG = "APP-Lobby-Act";
    private String username;
    private int PORT = 8888;

    private ListView peersListView;
    private ArrayAdapter<String> peersAdapter;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private WifiP2pManager manager;
    private WifiDirectBroadcastReceiver receiver;
    private WifiP2pDeviceList peers = new WifiP2pDeviceList();

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            // here to the listener
            Log.d(TAG, "peers available updated");
            // Out with the old, in with the new.
            ArrayList<WifiP2pDevice> peersNameFixed = new ArrayList<WifiP2pDevice>();

            for (WifiP2pDevice peer : peerList.getDeviceList()) {
                String newDeviceName = peer.deviceName.replace("[Phone]", "");
                peer.deviceName = newDeviceName;
            }
            peers = new WifiP2pDeviceList(peerList);

            // below here to service
            peersAdapter.clear();
            Log.d(TAG + "-PCHANGE", String.valueOf(peerList.getDeviceList().size()));

            // mock to code UI:
            if (peerList.getDeviceList().size() == 0) {
                peersAdapter.add("Mock dude");
                Log.d(TAG + "-PNAME", "Mock dude");
            }

            for (WifiP2pDevice peer : peerList.getDeviceList()) {
                peersAdapter.add(peer.deviceName); //+ "\n" + peer.deviceAddress
                Log.d(TAG + "-PNAME", peer.deviceName);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "not granted for fine location");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // Get user name set in MainActivity
        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.USER_NAME);

        TextView loggedInTextView = (TextView) findViewById(R.id.loggedIn);
        loggedInTextView.setText("You are logged in as " + username);


        // remove below here
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this, peerListListener);

        leaveGroups();

        setDeviceName(username);

        discoverPeers();

        peersListView = (ListView) findViewById(R.id.peersListView);
        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        peersListView.setEmptyView(emptyText);
        peersAdapter = new ArrayAdapter<String>(this, R.layout.fragment_peer, R.id.textView);
        setPeersList(peersAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "on resume called");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }

    public void discoverPeers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "not granted for fine location");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Called just to update users list
                Log.d(TAG, "discover peers sent");
            }

            @Override
            public void onFailure(int reason) {
                // Called just to update users list
                Log.d(TAG, "discover peers not sent: " + reason);
            }
        });
    }

    public void setPeersList(ArrayAdapter<String> peersAdapter) {
        peersListView = (ListView) findViewById(R.id.peersListView);
        peersListView.setAdapter(peersAdapter);
    }

    public void leaveGroups() {
        // makes sure to leave current group before entering a new one
        if (manager != null && channel != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "not granted for fine location");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }

            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && manager != null && channel != null) {
                        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "removeGroup onSuccess");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "removeGroup onFailure -" + reason);
                            }
                        });
                    }
                }
            });
        }
    }

    public void setDeviceName(String username) {
        // set name to discover based on the one typed by the user in previous activity
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = WifiP2pManager.Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = WifiP2pManager.ActionListener.class;

            Method m = manager.getClass().getMethod("setDeviceName", paramTypes);
            m.setAccessible(true);

            m.invoke(manager, channel, username, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Name change successful.");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "name change failed: " + reason);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "No such method");
        }
    }

    public void onRefresh(View view) {
        Log.d(TAG, "Refresh clicked");
        discoverPeers();
    }

}