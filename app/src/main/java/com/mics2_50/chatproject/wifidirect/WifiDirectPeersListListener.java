package com.mics2_50.chatproject.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiDirectPeersListListener implements WifiP2pManager.PeerListListener {
    private final String TAG = "WDS-Peers_Listener";

    private final WifiDirectController controller;

    public WifiDirectPeersListListener(WifiDirectController controller) {
        Log.d(TAG, "Created");
        this.controller = controller;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(TAG, "Peers list changed");

        for (WifiP2pDevice peer : peerList.getDeviceList()) {
            peer.deviceName = peer.deviceName.replace("[Phone]", "");
        }
        WifiP2pDeviceList peers = new WifiP2pDeviceList(peerList);

        if (peers.getDeviceList().size() == 0) {
            Log.d(TAG, "Update peers with mock");
            controller.updatePeersAdapterWithMock();
        } else {
            Log.d(TAG, "Update peers with list");
            controller.updatePeersAdapter(peers);
        }

    }
}
