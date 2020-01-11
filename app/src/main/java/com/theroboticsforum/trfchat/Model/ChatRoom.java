package com.theroboticsforum.trfchat.Model;

import java.util.ArrayList;

public class ChatRoom {
    private String to;
    private String from;
    private String id;
    //private ArrayList<Message> messages = null;

    public ChatRoom()
    {
        //necessary empty constructor
    }

    public ChatRoom(String to, String from, String id) {
        this.to = to;
        this.from = from;
        this.id = id;

    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getId() {
        return id;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setId(String id) {
        this.id = id;
    }


}
