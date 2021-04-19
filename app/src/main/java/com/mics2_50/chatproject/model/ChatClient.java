package com.mics2_50.chatproject.model;

import android.util.Log;

import com.mics2_50.chatproject.ChatActivity;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ChatClient {
    private BufferedReader dataIn;
    private PrintWriter dataOut;
    private String name;
    private Listener listener;
    private ChatActivity activity;

    public ChatClient(String name,BufferedReader dataIn,PrintWriter dataOut, ChatActivity activity){
        this.dataIn=dataIn;
        this.dataOut=dataOut;
        this.name=name;
        this.activity=activity;

        listener=new Listener(dataIn);
    }

    public String getName(){return name;}

    public BufferedReader getDataIn(){return dataIn;}

    public PrintWriter getDataOut(){return dataOut;}

    public void startListening(){
        listener.listening=true;
        listener.start();
    }

    public void stopListening(){
        listener.listening=false;
    }

    public class Listener extends Thread {
        BufferedReader input;
        boolean listening;

        public Listener(BufferedReader input) {
            this.input=input;
        }

        @Override
        public void run(){
            Log.d("Chat","Started listening to client");
            while(listening){
                try{
                    String data;
                    if((data=input.readLine())!=null){
                        Log.d("Chat","Listener calling receive");
                        activity.receive(data);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
