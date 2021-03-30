package com.mics2_50.chatproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.mics2_50.chatproject.wifidirect.WifiDirectController;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "CHAT-WIFIBROADREC";
    private WifiP2pManager manager;
    private Channel channel;
    private Activity activity;
    private PeerListListener peerListListener;
    private WifiDirectController controller;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Activity activity, WifiDirectController controller, PeerListListener peerListListener) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.peerListListener = peerListListener;
        this.controller = controller;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d(TAG, "Wifi P2P on");
                } else {
                    Log.d(TAG, "Wifi P2P off");
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                Log.d(TAG, "requested peers");
                // peers list changed
                if (manager != null) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG+"-PERM", "not granted for fine location");
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    }
                    manager.requestPeers(channel, peerListListener);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                if (manager == null) {
                    return;
                }
                // connection state changed - new connections or lost connections
                Log.d(TAG, "connection changed");
                manager.requestConnectionInfo(channel, (WifiP2pManager.ConnectionInfoListener) controller);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                Log.d(TAG, "device changed - get peers");
                // Device's wifi state changing
                if (manager != null) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG+"-PERM", "not granted for fine location");
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    }
                    manager.requestPeers(channel, peerListListener);
                }
                break;
            default:
                break;
        }
    }
}
