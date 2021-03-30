package com.mics2_50.chatproject.wifidirect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.core.app.ActivityCompat;

import com.mics2_50.chatproject.R;

import java.lang.reflect.Method;

public class WifiDirectController implements WifiP2pManager.ConnectionInfoListener {
    private final String TAG = "WDS-Main";
//    private final int PORT = 8888;

    private final IntentFilter intentFilter = new IntentFilter();

    private final WifiP2pManager.Channel channel;
    private final WifiP2pManager manager;
    private final WifiDirectBroadcastReceiver receiver;

    private final ArrayAdapter<String> peersAdapter;
    private final Activity activity;

    public WifiDirectController(Activity activity) {
        WifiDirectPeersListListener peersListListener = new WifiDirectPeersListListener(this);
        this.activity = activity;

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, activity.getMainLooper(), null);
        receiver = new WifiDirectBroadcastReceiver(manager, channel, activity, this, peersListListener);

        peersAdapter = new ArrayAdapter<>(activity, R.layout.fragment_peer, R.id.textView);

        discoverPeers();
    }

    public ArrayAdapter<String> getPeersAdapter() {
        return this.peersAdapter;
    }

    public void updatePeersAdapter(WifiP2pDeviceList peers) {
        peersAdapter.clear();
        for (WifiP2pDevice peer : peers.getDeviceList()) {
            peersAdapter.add(peer.deviceName);
            Log.d(TAG + "-AddPeer", peer.deviceName);
        }
    }

    public void updatePeersAdapterWithMock() {
        peersAdapter.clear();
        peersAdapter.add("Mock dude");
        Log.d(TAG + "-AddPeer", "Mock dude");
    }

    public void discoverPeers() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "not granted for fine location");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
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

    public void registerReceiver() {
        activity.registerReceiver(receiver, intentFilter);
    }

    public void unregisterReceiver() {
        activity.unregisterReceiver(receiver);
    }

    public void leaveGroups() {
        // makes sure to leave current group before entering a new one
        if (manager != null && channel != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "not granted for fine location");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }

            manager.requestGroupInfo(channel, group -> {
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
            });
        }
    }

    public void setDeviceName(String username) {
        // set name to discover based on the one typed by the user in previous activity
        try {
            Method m = manager.getClass().getMethod("setDeviceName", WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
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

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }
}
