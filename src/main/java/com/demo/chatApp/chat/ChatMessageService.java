package com.demo.chatApp.chat;


import com.demo.chatApp.chatroom.ChatRoomService;
import com.demo.chatApp.keyManager.Encryption;
import com.demo.chatApp.keyManager.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Service
public class ChatMessageService {
    private ChatMessageRepository repository;
    private ChatNotificationService chatNotificationService;
    private ChatRoomService chatRoomService;
    private KeyRepository keyRepository;
    private Encryption encryption;

    @Autowired
    public ChatMessageService(ChatMessageRepository repository, ChatRoomService chatRoomService, ChatNotificationService chatNotificationService){
        this.repository=repository;
        this.chatRoomService=chatRoomService;
        this.chatNotificationService= chatNotificationService;
    }



    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow(); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);

        System.out.println("chat message from save method :"+ chatMessage.getStatus());

        chatNotificationService.incrementSentStatusCountByChatId(chatId,chatMessage.getRecipientId());

        boolean retval =chatNotificationService.markMessageDeliveredForChatId(chatId,chatMessage.getSenderId());
        if (retval){
            System.out.println("Message was seen by "+chatMessage.getSenderId());
        }
        else{
            System.out.println("Sender is sending first message to this recipient "+chatMessage.getRecipientId());
        }
        markMessageDelivered(chatId,chatMessage.getSenderId());
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }

    //here sender will mark all the messages as delivered by recipient
    public boolean markMessageDelivered(String chatId,String senderId){

       if (chatId==null){
           System.out.println("No new message to be seen");
           return false;
       }
        repository.markMessageDeliveredRecipient(String.valueOf(chatId),senderId);
       return true;

    }

}
