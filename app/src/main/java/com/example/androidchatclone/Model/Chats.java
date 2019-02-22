package com.example.androidchatclone.Model;

public class Chats {

    String inboxKey;
    String senderUid;
    String message;

    public Chats(String inboxKey, String senderUid, String message) {
        this.inboxKey = inboxKey;
        this.senderUid = senderUid;
        this.message = message;
    }

    public Chats() {
    }

    public String getInboxKey() {
        return inboxKey;
    }

    public void setInboxKey(String inboxKey) {
        this.inboxKey = inboxKey;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
