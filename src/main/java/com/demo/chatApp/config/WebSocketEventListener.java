package com.demo.chatApp.config;

import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class WebSocketEventListener {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;
    private static final Set<String> activeUsers = ConcurrentHashMap.newKeySet();

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            String username = user.getName();
            activeUsers.add(username);
            logger.info("✅ User connected from event listener: " + username);

            // Notify all clients about the updated user list
            messagingTemplate.convertAndSend("/topic/onlineUsers", activeUsers);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            String username = user.getName();
            activeUsers.remove(username);
            logger.info("❌ User disconnected from event listener: " + username);

            // Notify all clients about the updated user list
            messagingTemplate.convertAndSend("/topic/onlineUsers", activeUsers);
        }
    }
}
