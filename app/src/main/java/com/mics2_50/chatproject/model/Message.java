package com.mics2_50.chatproject.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    private String TAG = "MSG-MODEL";

    private String text;
    private String username;
    private Integer avatarId;
    private boolean fromUser;

    public Message(String string, Integer avatarId) {
        try {
            JSONObject json = new JSONObject(string);
            this.text = (String) json.get("text");
            this.fromUser = (boolean) json.get("fromUser");
            this.username = (String) json.get("username");
//            this.time = (String) json.get("time");
            this.avatarId = avatarId;
        } catch (JSONException e) {
            Log.d(TAG, "couldn't parse JSON string: " + e.getMessage());
        }
    }

    public Message(String text, String username, boolean fromUser) {
        this.text = text;
        this.username = username;
        this.fromUser = fromUser;
    }

    public String getText() {
        return this.text;
    }

    public Integer getAvatarId() {
        return this.avatarId;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isFromUser() {
        return this.fromUser;
    }

    public String getJSONString(String sender) {
        String string;
        try {
            JSONObject json = new JSONObject()
                    .put("text", text)
                    .put("fromUser", false)
                    .put("username", sender);
//                    .put("time", time);
            string = json.toString();
        } catch (JSONException e) {
            Log.d(TAG, "couldn't construct JSONObject: " + e.getMessage());
            string = "";
        }
        return string;
    }
}
