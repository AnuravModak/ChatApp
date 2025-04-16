package com.demo.chatApp.chat;



import com.demo.chatApp.chatroom.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ChatController {

    private SimpMessagingTemplate messagingTemplate;
    private  ChatMessageService chatMessageService;
    private ChatRoomService chatRoomService;
    private ChatNotificationService chatNotificationService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService,
                          ChatRoomService chatRoomService, ChatNotificationService chatNotificationService){
        this.messagingTemplate=messagingTemplate;
        this.chatMessageService=chatMessageService;
        this.chatRoomService= chatRoomService;
        this.chatNotificationService=chatNotificationService;
    }


    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getStatus()== null || chatMessage.getStatus()==MessageStatus.SENT){
            chatMessage.setStatus(MessageStatus.SENT);
            ChatMessage savedMsg = chatMessageService.save(chatMessage, 0);
        }
        else{
            ChatMessage savedMsg = chatMessageService.save(chatMessage, 1);
        }

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "queue/messages",
                chatMessage
        );
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "queue/Notifications",
                chatNotificationService.getAllNotificationsForUser(chatMessage.getRecipientId())
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessagesAndMarkDelivered(@PathVariable String senderId,
                                                              @PathVariable String recipientId) {
        var chatId = chatRoomService
                .getChatRoomId(senderId, recipientId, true)
                .orElseThrow();
        System.out.println("Calling inside findchatmessage "+senderId+" "+ recipientId +" "+ chatId);
        //this is the call to update chatmessage status as delivered
        boolean tempval=chatMessageService.markMessageDelivered(chatId,senderId);

        // now this is the call to update chatnotification
        chatNotificationService.markMessageDeliveredForChatId(chatId, senderId);

        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    // this is independent api gateway to mark message as delivered
    @GetMapping("/messages/{senderId}/{recipientId}/markMessageSeen")
    public ResponseEntity<String> markDelivered(@PathVariable String senderId,
                                                @PathVariable String recipientId) {
        try {
            var chatId = chatRoomService
                    .getChatRoomId(senderId, recipientId, false)
                    .orElseThrow();

            //this is the call to update chatmessage status as delivered
            boolean tempval = chatMessageService.markMessageDelivered(chatId,senderId);

            // now this is the call to update chatnotification
            chatNotificationService.markMessageDeliveredForChatId(chatId, senderId);
            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    // get notification summary when a user logs in
    @GetMapping ("/messages/{senderId}/allNotifications")
    public List<ChatNotification> getAllNotifications(@PathVariable String senderId) {

        List<ChatNotification> chatNotifications=chatNotificationService.getAllNotificationsForUser(senderId);
        System.out.println("List of notifications");
        return chatNotifications;
    }
}
