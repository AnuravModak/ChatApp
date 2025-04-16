package com.demo.chatApp.chat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatNotificationService {

    private ChatNotificationRepository chatNotificationRepository;

    @Autowired
    public  ChatNotificationService(ChatNotificationRepository chatNotificationRepository){
        this.chatNotificationRepository=chatNotificationRepository;

    }

    public void incrementSentStatusCountByChatId(String chatId, String recipientId) {
        if (chatId == null || chatId.isEmpty() || recipientId == null || recipientId.isEmpty()) {
            throw new IllegalArgumentException("chatId and recipientId must not be null or empty");
        }

        try {
            Optional<ChatNotification> optionalNotification =
                    chatNotificationRepository.findByChatIdAndRecipientId(chatId, recipientId);

            if (optionalNotification.isPresent()) {
                chatNotificationRepository.incrementSentCount(chatId, recipientId);
            } else {
                System.out.println("Inside this 2.. ");
                ChatNotification notification = new ChatNotification(chatId, recipientId);
                chatNotificationRepository.save(notification);
            }
        } catch (Exception e) {
            System.err.println("Error incrementing sent status count: " + e.getMessage());
            // optionally rethrow or log to a logger
            throw new RuntimeException("Failed to increment sent status count", e);
        }
    }

    public List<ChatNotification> getAllNotificationsForUser(String recipientId) {
        if (recipientId == null || recipientId.isEmpty()) {
            throw new IllegalArgumentException("recipientId must not be null or empty");
        }

        try {
            return chatNotificationRepository.getAllNotificationsForUser(recipientId);
        } catch (Exception e) {
            System.err.println("Error retrieving notifications for user: " + e.getMessage());
            return null ; // fail gracefully
        }
    }

    public boolean markMessageDeliveredForChatId(String chatId, String recipientId) {
        if (chatId == null || chatId.isEmpty() || recipientId == null || recipientId.isEmpty()) {
            throw new IllegalArgumentException("chatId and recipientId must not be null or empty");
        }

        try {
            Optional<ChatNotification> optionalNotification =
                    chatNotificationRepository.findByChatIdAndRecipientId(chatId, recipientId);

            if (optionalNotification.isPresent()) {
                chatNotificationRepository.markMessageReadByChatId(chatId, recipientId);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error marking message delivered: " + e.getMessage());
        }

        return false;
    }


}
