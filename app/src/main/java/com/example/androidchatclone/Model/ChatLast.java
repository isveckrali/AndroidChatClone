package com.example.androidchatclone.Model;

public class ChatLast {

    String inboxKey;
    String messageKey;

    public ChatLast(String inboxKey, String messageKey) {
        this.inboxKey = inboxKey;
        this.messageKey = messageKey;
    }

    public ChatLast() {

    }

    public String getInboxKey() {
        return inboxKey;
    }

    public void setInboxKey(String inboxKey) {
        this.inboxKey = inboxKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
