package com.mics2_50.chatproject.wifidirect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.mics2_50.chatproject.ChatActivity;
import com.mics2_50.chatproject.MainActivity;
import com.mics2_50.chatproject.R;
import com.mics2_50.chatproject.adapter.PeerAdapter;

import java.lang.reflect.Method;
import java.net.InetAddress;

public class WifiDirectController implements WifiP2pManager.ConnectionInfoListener {
    private final String TAG = "WDS-Main";
    public static final String USER_INFO = "com.mics2_50.chatproject.USER_INFO";
    public static final String PEER_NAME = "com.mics2_50.chatproject.PEER_NAME";

    private final IntentFilter intentFilter = new IntentFilter();

    private final WifiP2pManager.Channel channel;
    private final WifiP2pManager manager;
    private final WifiDirectBroadcastReceiver receiver;

    private String[] deviceNames;
    private Integer[] deviceAvatars;
    private WifiP2pDevice[] devices;
    private final PeerAdapter peersAdapter;
    private ListView peersView;
    private final Activity activity;
    private boolean isMock;
    private String username;
    private String peername;

    public WifiDirectController(Activity activity, String username) {
        WifiDirectPeersListListener peersListListener = new WifiDirectPeersListListener(this);
        this.activity = activity;
        this.username = username;
        this.peername = "";

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, activity.getMainLooper(), null);
        receiver = new WifiDirectBroadcastReceiver(manager, channel, activity, this, peersListListener);

        peersAdapter = new PeerAdapter(activity);
        peersView = (ListView) activity.findViewById(R.id.peersListView);
        peersView.setAdapter(peersAdapter);

        discoverPeers();
    }

    public PeerAdapter getPeersAdapter() {
        return this.peersAdapter;
    }

    public void updatePeersAdapter(WifiP2pDeviceList peers) {
        peersAdapter.clear();
        this.isMock = false;

        deviceNames = new String[peers.getDeviceList().size()];
        deviceAvatars = new Integer[peers.getDeviceList().size()];
        devices = new WifiP2pDevice[peers.getDeviceList().size()];

        int index = 0;
        for (WifiP2pDevice peer : peers.getDeviceList()) {
            Log.d(TAG + "-AddPeer", peer.deviceName);

            deviceNames[index] = peer.deviceName;
            deviceAvatars[index] = getAvatarId(index+1);
            devices[index] = peer;

            index++;
        }

        deviceNames = new String[1];
        deviceNames[0] = "Mock";
        deviceAvatars = new Integer[1];
        deviceAvatars[0] = getAvatarId(1);

        peersAdapter.addAll(deviceNames, deviceAvatars);
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
        // set name to discover based on the one typed by the user
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

    private Integer getAvatarId(int index) {
        return R.drawable.avatar;
    }

    public void connectToPeer(int i) {
        this.peername = deviceNames[i];
        final WifiP2pDevice device = devices[i];
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "not granted for fine location");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "connection sent to " + deviceNames[i]);
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "connection not sent to " + deviceNames[i] + ": " + reason);
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if(info.groupFormed) {
            Log.d(TAG, "onConnectionInfoAvailable - Group formed");
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(USER_INFO, info);
            intent.putExtra(MainActivity.USER_NAME, username);
            intent.putExtra(PEER_NAME, peername);
            activity.startActivityForResult(intent, 1);
        } else {
            Log.d(TAG, "onConnectionInfoAvailable - Group not formed");
        }
    }
}
