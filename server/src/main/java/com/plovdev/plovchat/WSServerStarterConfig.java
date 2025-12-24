package com.plovdev.plovchat;

import com.plovdev.plovchat.ws.ChatWebSocketServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WSServerStarterConfig {
    private final ChatWebSocketServer webSocketServer;

    @PostConstruct
    public void startWebSocketServer() {
        if (!webSocketServer.isOpen) {
            log.info("WebSocket сервер запущен на порту {}", webSocketServer.getPort());
        }
    }
}