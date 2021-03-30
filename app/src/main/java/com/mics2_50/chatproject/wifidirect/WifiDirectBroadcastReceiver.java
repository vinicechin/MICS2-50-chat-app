package com.mics2_50.chatproject.wifidirect;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "WDS-Broad-Rec";

    private final WifiP2pManager manager;
    private final Channel channel;
    private final Activity activity;
    private final PeerListListener peerListListener;
    private final WifiDirectController controller;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Activity activity, WifiDirectController controller, PeerListListener peerListListener) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.peerListListener = peerListListener;
        this.controller = controller;
        Log.d(TAG, "Created");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                verifyWifiP2pState(intent);
                break;

            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                // peers list changed
                Log.d(TAG, "Requested peers");
                requestPeers();
                break;

            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                if (manager == null) {
                    return;
                }
                // connection state changed - new connections or lost connections
                Log.d(TAG, "Connection changed");
                manager.requestConnectionInfo(channel, controller);
                break;

            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                // Device's wifi state changing
                Log.d(TAG, "Device changed - get peers");
                requestPeers();
                break;

            default:
                break;
        }
    }

    private void verifyWifiP2pState(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            Log.d(TAG, "Wifi P2P on");
        } else {
            Log.d(TAG, "Wifi P2P off");
        }
    }

    private void requestPeers() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG+"-Perm", "Not granted for fine location");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            manager.requestPeers(channel, peerListListener);
        }
    }
}
