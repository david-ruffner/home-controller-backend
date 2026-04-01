package com.davidruffner.homecontrollerbackend.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InventoryWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connected: " + session.getId());
        sessions.add(session);
        session.sendMessage(new TextMessage("Connected to /ws"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        for (WebSocketSession wsSession : sessions) {
            if (wsSession.isOpen()) {
                wsSession.sendMessage(new TextMessage(message.getPayload()));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
        org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
