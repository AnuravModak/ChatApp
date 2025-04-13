package com.demo.chatApp.chat;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String chatId);

    // This handles both directions (sender -> recipient or recipient -> sender)
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :senderId AND m.recipientId = :recipientId) OR " +
            "(m.senderId = :recipientId AND m.recipientId = :senderId) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findMessagesBetweenUsers(@Param("senderId") String senderId,
                                               @Param("recipientId") String recipientId);

    @Query("SELECT m.senderId, COUNT(m) FROM ChatMessage m WHERE m.recipientId = :recipientId AND m.status = 'SENT' GROUP BY m.senderId")
    List<Object[]> countUnreadMessagesGroupedBySender(@Param("recipientId") String recipientId);


    @Query("SELECT m FROM ChatMessage m WHERE m.recipientId = :recipientId AND m.status = :status GROUP BY m.senderId")
    List<ChatMessage> findByRecipientIdAndStatusGroupedBySender(String recipientId, MessageStatus status);

    @Query("SELECT m FROM ChatMessage m WHERE m.recipientId = :recipientId AND m.status = :status ")
    List<ChatMessage> findByRecipientIdAndStatus(String recipientId, MessageStatus status);

    List<ChatMessage> findBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status);

    @Transactional
    @Modifying
    @Query("UPDATE ChatMessage m SET m.status = 'DELIVERED' " +
            "WHERE m.chatId = :chatId AND m.recipientId = :senderId AND m.status <> 'DELIVERED'")
    void markMessageDeliveredRecipient(@Param("chatId") String chatId,
                              @Param("senderId") String senderId);
//    void save(ChatMessage );
}
