package com.theroboticsforum.trfchat.Model;

public class Message {
    private String message;
    private String senderEmail;

    public Message()
    {
        //necessary no-argument contructor
    }


    public Message(String message, String senderEmail) {
        this.message = message;
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
}
