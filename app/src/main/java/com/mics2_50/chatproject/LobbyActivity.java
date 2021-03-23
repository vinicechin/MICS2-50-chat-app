package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {
    private final String TAG = "CHAT-LOBBYACT";
    private String name;
    private int PORT = 8888;

    private ListView peersListView;
    private ArrayAdapter<String> peersAdapter;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private WifiP2pManager manager;
    private BroadcastReceiver receiver;
    private WifiP2pDeviceList peers = new WifiP2pDeviceList();

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Log.d(TAG, "peers available");
            // Out with the old, in with the new.
            ArrayList<WifiP2pDevice> peersNameFixed = new ArrayList<WifiP2pDevice>();

            for (WifiP2pDevice peer : peerList.getDeviceList()) {
                String newDeviceName = peer.deviceName.replace("[Phone]","");
                peer.deviceName = newDeviceName;
            }
            peers = new WifiP2pDeviceList(peerList);

            peersAdapter.clear();
            for (WifiP2pDevice peer : peerList.getDeviceList()) {
                peersAdapter.add(peer.deviceName); //+ "\n" + peer.deviceAddress
                Log.d(TAG+"-PNAME", peer.deviceName);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Called just to update users list
            }

            @Override
            public void onFailure(int reason) {
                // Called just to update users list
            }
        });

        peersListView = (ListView) findViewById(R.id.peersListView);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        peersListView.setEmptyView(emptyText);
        peersAdapter = new ArrayAdapter<String>(this, R.layout.fragment_peer, R.id.textView);

        peersListView.setAdapter(peersAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this, peerListListener);
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
}