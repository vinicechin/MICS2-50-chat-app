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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        controller = new WifiDirectController(this);

        setUserName();
        setPeersList();

        controller.leaveGroups();
        controller.discoverPeers();
    }

    private void setUserName() {
        // Get user name set in MainActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.USER_NAME);

        TextView loggedInTextView = findViewById(R.id.loggedIn);
        loggedInTextView.setText(String.format("Welcome %s!", username));

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
            boolean result = controller.connectToPeer(peername);

            if (!result) {
                Toast toast = Toast.makeText(this, "Couldn't connect to " + peername, Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
                toast.show();
            }
        });
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

}