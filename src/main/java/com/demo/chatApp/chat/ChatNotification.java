package com.demo.chatApp.chat;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity()
public class ChatNotification {

    @Id
    private String id;
    private String chatId;
    private String recipientId;

    @Column(name = "sentStatusCount")
    private int sentStatusCount;

    @Column(name = "deliveredStatusCount")
    private int deliveredStatusCount;

    public ChatNotification() {
        this.id=UUID.randomUUID().toString();
    }

    public ChatNotification(String id, String chatId,String recipientId, int sentStatusCount, int deliveredStatusCount) {
        this.id = id;
        this.chatId = chatId;
        this.recipientId = recipientId;
        this.sentStatusCount = sentStatusCount;
        this.deliveredStatusCount = deliveredStatusCount;
    }

    public ChatNotification(String chatId, String recipientId, int sentStatusCount, int deliveredStatusCount) {
        this.id=UUID.randomUUID().toString();
        this.chatId = chatId;
        this.recipientId = recipientId;
        this.sentStatusCount = sentStatusCount;
        this.deliveredStatusCount = deliveredStatusCount;
    }

    public ChatNotification(String chatId, String recipientId) {
        this.id = UUID.randomUUID().toString();
        this.chatId = chatId;
        this.recipientId = recipientId;
        this.sentStatusCount = 1;      // <-- one new message sent
        this.deliveredStatusCount = 0; // <-- not yet delivered
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

    public int getsentStatusCount() {
        return sentStatusCount;
    }

    public void setsentStatusCount(int sentStatusCount) {
        this.sentStatusCount = sentStatusCount;
    }

    public int getdeliveredStatusCount() {
        return deliveredStatusCount;
    }

    public void setdeliveredStatusCount(int deliveredStatusCount) {
        this.deliveredStatusCount = deliveredStatusCount;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
}
