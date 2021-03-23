package com.mics2_50.chatproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "CHAT-WIFIBROADREC";
    private WifiP2pManager manager;
    private Channel channel;
    private Activity activity;
    private PeerListListener peerListListener;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Activity activity, PeerListListener peerListListener) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.peerListListener = peerListListener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//                    activity.setIsWifiP2pEnabled(true);
                } else {
//                    activity.setIsWifiP2pEnabled(false);
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                // peers list changed
                if (manager != null) {
                    Log.d(TAG, "requested peers");
                    manager.requestPeers(channel, peerListListener);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                // connection state changed - new connections or lost connections
                Log.d(TAG, "connection changed");
                manager.requestConnectionInfo(channel, (WifiP2pManager.ConnectionInfoListener) activity);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                // Device's wifi state changing
                break;
            default:
                break;
        }
    }
}
