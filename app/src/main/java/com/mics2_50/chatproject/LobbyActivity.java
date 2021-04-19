package com.mics2_50.chatproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mics2_50.chatproject.wifidirect.WifiDirectController;

public class LobbyActivity extends AppCompatActivity {
    private final String TAG = "APP-Lobby-Act";

    private ListView peersListView;
    private WifiDirectController controller;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Get user name set in MainActivity
        Intent intent = getIntent();
        this.username = intent.getStringExtra(MainActivity.USER_NAME);

        controller = new WifiDirectController(this, this.username);

        setUserName();
        setPeersList();

        controller.leaveGroups();
        controller.discoverPeers();
    }

    private void setUserName() {
        TextView loggedInTextView = findViewById(R.id.loggedIn);
        loggedInTextView.setText(String.format("Welcome %s!", this.username));

        controller.setDeviceName(username);
    }

    private void setPeersList() {
        peersListView = findViewById(R.id.peersListView);
        TextView emptyText = findViewById(android.R.id.empty);
        peersListView.setEmptyView(emptyText);
        peersListView = findViewById(R.id.peersListView);
        peersListView.setAdapter(controller.getPeersAdapter());
        peersListView.setOnItemClickListener((parent, view, position, id) -> {
            TextView item = view.findViewById(R.id.textView);
            String peername = item.getText().toString();

            Log.d(TAG, "Chat clicked: " + peername);
            controller.connectToPeer(position);
        });
    }

    public void connectionWithPeerSuccess(String peername) {
        Toast toast = Toast.makeText(this, "Connected to  " + peername, Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }

    public void connectionWithPeerFail(String peername) {
        Toast toast = Toast.makeText(this, "Couldn't connect to " + peername, Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "on resume called");
        controller.registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "on pause called");
        controller.unregisterReceiver();
    }

    public void onRefresh(View view) {
        Log.d(TAG, "Refresh clicked");
        controller.discoverPeers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            controller.leaveGroups();
        }
    }

}