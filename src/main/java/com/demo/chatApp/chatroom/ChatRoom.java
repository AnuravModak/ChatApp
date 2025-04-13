package com.demo.chatApp.chatroom;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class ChatRoom {

    @Id
    private String id;

    @Column(length = 2048)
    private String chatId;

    @Column(length = 2048)
    private String senderId;

    @Column(length = 2048)
    private String recipientId;

    public ChatRoom() {
        this.id = UUID.randomUUID().toString();
    }

    public ChatRoom( String chatId, String senderId, String recipientId) {
        this.id =UUID.randomUUID().toString();
        this.chatId = chatId;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    public ChatRoom(String id, String chatId, String senderId, String recipientId) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
}
