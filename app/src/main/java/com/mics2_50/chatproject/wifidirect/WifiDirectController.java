package com.mics2_50.chatproject.wifidirect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.core.app.ActivityCompat;

import com.mics2_50.chatproject.LobbyActivity;
import com.mics2_50.chatproject.R;
import com.mics2_50.chatproject.WifiDirectBroadcastReceiver;

import java.lang.reflect.Method;

public class WifiDirectController implements WifiP2pManager.ConnectionInfoListener {
    private final String TAG = "WDS-Main";

    private final IntentFilter intentFilter = new IntentFilter();

    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private WifiDirectBroadcastReceiver receiver;
    private WifiP2pDeviceList peers = new WifiP2pDeviceList();
    private WifiDirectPeersListListener peersListListener;

    private ArrayAdapter<String> peersAdapter;
    private Activity activity;

    public WifiDirectController(Activity activity) {
        this.activity = activity;
        this.peersListListener = new WifiDirectPeersListListener(this.activity, this);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, activity.getMainLooper(), null);
        receiver = new WifiDirectBroadcastReceiver(manager, channel, activity, peersListListener);

        peersAdapter = new ArrayAdapter<String>(activity, R.layout.fragment_peer, R.id.textView);

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

        // verify if needed
        // ((LobbyActivity) activity).setPeersList(peersAdapter);
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

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }
}
