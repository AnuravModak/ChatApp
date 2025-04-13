package com.demo.chatApp.chat;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatNotificationRepository extends JpaRepository<ChatNotification, String> {
    @Transactional
    @Modifying
    @Query("UPDATE ChatNotification n SET n.sentStatusCount = CAST(n.sentStatusCount AS int) + 1 " +
            "WHERE n.chatId = :chatId AND n.recipientId = :recipientId")
    void incrementSentCount(@Param("chatId") String chatId,
                            @Param("recipientId") String recipientId);

    @Query("SELECT c FROM ChatNotification c WHERE c.chatId = :chatId AND c.recipientId = :recipientId")
    Optional<ChatNotification> findByChatIdAndRecipientId(@Param("chatId") String chatId,
                                                          @Param("recipientId") String recipientId);

    @Query("SELECT c FROM ChatNotification c WHERE c.recipientId = :recipientId AND c.sentStatusCount <> c.deliveredStatusCount")
    List<ChatNotification> getAllNotificationsForUser(@Param("recipientId") String recipientId);

    @Transactional
    @Modifying
    @Query("UPDATE ChatNotification n SET n.deliveredStatusCount = n.sentStatusCount " +
            "WHERE n.chatId = :chatId AND n.recipientId = :recipientId")
    void markMessageReadByChatId(@Param("chatId") String chatId,
                                 @Param("recipientId") String recipientId);

}
