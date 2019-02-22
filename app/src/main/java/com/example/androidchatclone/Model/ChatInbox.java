package com.example.androidchatclone.Model;

public class ChatInbox {

    String inboxKey;
    String senderUid;
    String recipientUid;
    String isRead;

    public ChatInbox(String inboxKey, String senderUid, String recipientUid, String isRead) {
        this.inboxKey = inboxKey;
        this.senderUid = senderUid;
        this.recipientUid = recipientUid;
        this.isRead = isRead;
    }

    public ChatInbox() {

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

    public String getRecipientUid() {
        return recipientUid;
    }

    public void setRecipientUid(String recipientUid) {
        this.recipientUid = recipientUid;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }


    @Override
    public String toString() {
        return "ChatInbox{" +
                "inboxKey='" + inboxKey + '\'' +
                ", senderUid='" + senderUid + '\'' +
                ", recipientUid='" + recipientUid + '\'' +
                ", isRead='" + isRead + '\'' +
                '}';
    }
}
