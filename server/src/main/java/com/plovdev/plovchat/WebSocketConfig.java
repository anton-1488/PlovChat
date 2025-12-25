package com.plovdev.plovchat;

import com.plovdev.plovchat.repos.ChatMemberRepository;
import com.plovdev.plovchat.repos.ChatRepository;
import com.plovdev.plovchat.repos.MessageRepos;
import com.plovdev.plovchat.repos.UsersRepository;
import com.plovdev.plovchat.ws.ChatWebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    private final UsersRepository usersRepository;
    private final MessageRepos messageRepos;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Bean
    public ChatWebSocketServer chatWebSocketServer() {
        ChatWebSocketServer server = new ChatWebSocketServer(8081);
        server.setRepositories(usersRepository, messageRepos, chatRepository, chatMemberRepository);
        return server;
    }
}