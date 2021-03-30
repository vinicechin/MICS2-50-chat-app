package com.mics2_50.chatproject.wifidirect;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiDirectPeersListListener implements WifiP2pManager.PeerListListener {
    private final String TAG = "WDS-Peers_Listener";

    private WifiP2pDeviceList peers = new WifiP2pDeviceList();

    private Activity activity;
    private WifiDirectController controller;

    public WifiDirectPeersListListener(Activity activity, WifiDirectController controller) {
        this.activity = activity;
        this.controller = controller;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(TAG, "peers available updated");

        for (WifiP2pDevice peer : peerList.getDeviceList()) {
            String newDeviceName = peer.deviceName.replace("[Phone]", "");
            peer.deviceName = newDeviceName;
        }
        peers = new WifiP2pDeviceList(peerList);

        if (peers.getDeviceList().size() == 0) {
            Log.d(TAG, "Update peers with mock");
            controller.updatePeersAdapterWithMock();
        } else {
            Log.d(TAG, "Update peers with list");
            controller.updatePeersAdapter(peers);
        }

    }
}
