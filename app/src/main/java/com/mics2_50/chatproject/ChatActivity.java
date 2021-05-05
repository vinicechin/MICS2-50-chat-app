package com.mics2_50.chatproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mics2_50.chatproject.adapter.MessageAdapter;
import com.mics2_50.chatproject.model.ChatClient;
import com.mics2_50.chatproject.model.Message;
import com.mics2_50.chatproject.wifidirect.WifiDirectController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class ChatActivity extends AppCompatActivity {
    private final String TAG = "APP-Chat-Act";
    private final int PORT = 8000;

    private EditText editTextMessage;
    private String username;
    private String peername;
    private Integer peerAvatarId;
    private WifiP2pInfo info;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private ServerSocket serverSocket;
    private Socket socket;
    private ChatClient client;
    private SocketMessageSender sender;
    BufferedReader fromGroupOwner;
    PrintWriter toGroupOwner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        // Get user name set in MainActivity
        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.USER_NAME);
        peername = intent.getStringExtra(WifiDirectController.PEER_NAME);
        peerAvatarId = intent.getIntExtra(WifiDirectController.PEER_AVATARID, R.drawable.avatar);
        Bundle bundle = intent.getExtras();
        info = (WifiP2pInfo) bundle.get(WifiDirectController.USER_INFO);

//        Message m1 = new Message("teste", "Mock", false);
//        String strMsg = m1.getJSONString("Mock");
//
//        m1 = new Message(strMsg, peerAvatarId);
//        messageAdapter.add(m1);
//        messagesView.setSelection(messagesView.getCount() - 1);

        // Set up socket connection between users: depend if is group owner or not
        if (info.isGroupOwner) {
            Log.d(TAG, "onConnectionInfoAvailable - Host");
            try {
                serverSocket = new ServerSocket(PORT);
            } catch (Exception e) {
                Log.d(TAG, "on create ServerSocket: "+ e.getMessage());
            }
            getClientInfo.start();
        } else {
            Log.d(TAG, "nConnectionInfoAvailable - Client");
            try{
                connectToOwner.start();
            }catch(Exception e){
                Log.d(TAG, "notGroupOwner "+ e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(final View view) {
        final String message = editTextMessage.getText().toString();
        if (message.length() > 0) {
            final Message msg = new Message(message, username, true);
            this.onMessage(msg);
            Log.d(TAG,"Executing sendMessage");

            SocketMessageSender sender = new SocketMessageSender(msg);
            sender.start();

            editTextMessage.getText().clear();
        }
    }

    public void onMessage(Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.add(message);
                // scroll the ListView to the last added element
                messagesView.setSelection(messagesView.getCount() - 1);
            }
        });
    }

    Thread connectToOwner = new Thread() {
        @Override
        public void run() {
            InetAddress groupOwner = info.groupOwnerAddress;
            socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(groupOwner.getHostAddress(), PORT));
                fromGroupOwner = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                toGroupOwner = new PrintWriter(socket.getOutputStream(), true);
                toGroupOwner.println(username);
                Log.d(TAG,"Name sent to Owner");

                String partnerName = fromGroupOwner.readLine();
                getSupportActionBar().setTitle(partnerName);
            }
            catch (IOException e) {
                Log.d(TAG,"Exception in connectToOwner" + e.getMessage());
                e.printStackTrace();
            }
            listenToGroupOwner.start();
        }
    };


    Thread getClientInfo = new Thread() {
        @Override
        public void run() {

            try{
                Log.d(TAG,"waiting for client");
                Socket clientSocket = serverSocket.accept();
                Log.d(TAG,"Client found");
                BufferedReader dataIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter dataOut = new PrintWriter(clientSocket.getOutputStream(),true);
                String clientName = dataIn.readLine();
                dataOut.println(username);
                client = new ChatClient(clientName, dataIn, dataOut, ChatActivity.this);
                client.startListening();

                Log.d(TAG, "client added");

                getSupportActionBar().setTitle(clientName);

            }catch(Exception e) {
                Log.d(TAG,"Exception in getClientInfo" + e.getMessage());
                e.printStackTrace();
            }
        }
    };

    Thread listenToGroupOwner = new Thread() {
        @Override
        public void run() {
            try{
                Log.d(TAG,"Started listening to GroupOwner");
                while(true){
                    String data;
                    if((data = fromGroupOwner.readLine()) != null){
                        Log.d(TAG,"Calling receive");
                        receive(data);
                    }
                }
            }catch(Exception e){
                Log.d(TAG,"Exception in listenToGroupOwner" + e.getMessage());
                e.printStackTrace();
            }
        }
    };

    public void receive(String data){
        Log.d(TAG,"received something:");
        Log.d(TAG,data);
        Message msg = new Message(data, peerAvatarId);
        this.onMessage(msg);
    }

    public class SocketMessageSender extends Thread {
        private Message message;

        public SocketMessageSender(Message message) {
            this.message = message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            Log.d("Chat","Sending Message");
            if(info.isGroupOwner){
                PrintWriter dataOut = client.getDataOut();
                dataOut.println(message.getJSONString(username));
                Log.d(TAG,"Group Owner sent Message");
            } else {
                toGroupOwner.println(message.getJSONString(username));
                Log.d(TAG,"Client sent message");
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(info.isGroupOwner) {
                client.stopListening();
                serverSocket.close();
            }else{
                socket.close();
            }
        }catch(Exception e){
        }
    }
}