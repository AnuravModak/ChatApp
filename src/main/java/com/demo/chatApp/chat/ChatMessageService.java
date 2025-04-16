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



    public ChatMessage save(ChatMessage chatMessage, int flag) {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow(); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);

        if (flag==0){
            chatNotificationService.incrementSentStatusCountByChatId(chatId,chatMessage.getRecipientId());
            boolean retval =chatNotificationService.markMessageDeliveredForChatId(chatId,chatMessage.getSenderId());
            markMessageDelivered(chatId,chatMessage.getSenderId());
            repository.save(chatMessage);
        }
        else if (flag==1){
            chatNotificationService.incrementSentStatusCountByChatId(chatId,chatMessage.getRecipientId());
            boolean retval =chatNotificationService.markMessageDeliveredForChatId(chatId,chatMessage.getSenderId());
            boolean retval1 =chatNotificationService.markMessageDeliveredForChatId(chatId,chatMessage.getRecipientId());
            markMessageDelivered(chatId,chatMessage.getSenderId());
            repository.save(chatMessage);
        }
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
