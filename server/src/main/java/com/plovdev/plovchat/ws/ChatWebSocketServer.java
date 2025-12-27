package com.plovdev.plovchat.ws;

import com.plovdev.plovchat.entities.ChatEntity;
import com.plovdev.plovchat.entities.ChatMember;
import com.plovdev.plovchat.entities.MessageEntity;
import com.plovdev.plovchat.entities.UserEntity;
import com.plovdev.plovchat.models.File;
import com.plovdev.plovchat.models.utils.JsonParser;
import com.plovdev.plovchat.repos.ChatMemberRepository;
import com.plovdev.plovchat.repos.ChatRepository;
import com.plovdev.plovchat.repos.MessageRepos;
import com.plovdev.plovchat.repos.UsersRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatWebSocketServer extends WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketServer.class);

    public boolean isOpen = false;

    private UsersRepository usersRepository;
    private MessageRepos messageRepos;
    private ChatRepository chatRepository;
    private ChatMemberRepository chatMemberRepository;

    // Хранилище подключений: userId -> WebSocket
    private final Map<String, WebSocket> userConnections = new HashMap<>();

    // Хранилище: WebSocket -> userId
    private final Map<WebSocket, String> socketToUser = new HashMap<>();

    public void setRepositories(UsersRepository usersRepository,
                                MessageRepos messageRepos,
                                ChatRepository chatRepository, ChatMemberRepository memberRepository) {
        this.usersRepository = usersRepository;
        this.messageRepos = messageRepos;
        this.chatRepository = chatRepository;
        chatMemberRepository = memberRepository;
    }

    public ChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @PostConstruct
    public void init() {
        log.info("Инициализация WebSocket сервера...");
        this.start();
        log.info("WebSocket сервер запущен на порту {}", getPort());
    }

    @PreDestroy
    public void cleanup() {
        log.info("Остановка WebSocket сервера...");
        try {
            this.stop();
        } catch (Exception e) {
            log.error("Ошибка при остановке WebSocket сервера", e);
        }
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.info("Новое подключение: {}", conn.getRemoteSocketAddress());
        isOpen = true;
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JSONObject json = new JSONObject(message);
            String op = json.getString("op");

            switch (op) {
                case "login":
                    handleLogin(conn, json);
                    break;
                case "message":
                    handleMessage(conn, json);
                    break;
                case "status":
                    handleStatus(conn, json);
                    break;
            }
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: {}", e.getMessage());
            conn.send("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleLogin(WebSocket conn, JSONObject json) {
        try {
            JSONArray array = json.getJSONArray("args");
            String userId = array.getJSONObject(0).getString("id");
            String userPassword = array.getJSONObject(0).getString("password");

            // Проверяем пользователя в БД
            UserEntity user = usersRepository.findByIdAndPassword(Long.parseLong(userId), userPassword);
            boolean success = user != null;

            if (success) {
                // Сохраняем связь
                userConnections.put(userId, conn);
                socketToUser.put(conn, userId);

                log.info("Пользователь подключен: {}", userId);
            }

            // Отправляем ответ
            JSONObject response = new JSONObject();
            response.put("op", "login");
            response.put("args", new JSONObject().put("success", success));
            conn.send(response.toString());

        } catch (Exception e) {
            log.error("Ошибка аутентификации: {}", e.getMessage());
            JSONObject error = new JSONObject();
            error.put("op", "login");
            error.put("args", new JSONObject().put("success", false));
            conn.send(error.toString());
        }
    }

    private void handleMessage(WebSocket conn, JSONObject json) {
        String senderId = socketToUser.get(conn);
        if (senderId == null) {
            conn.send("{\"error\":\"Not authenticated\"}");
            return;
        }

        JSONObject args = json.getJSONObject("args");

        String text = args.getString("text");
        String chatId = args.getString("chatId");
        String messageType = args.getString("type");

        Optional<ChatEntity> chatEntityOptional = chatRepository.findById(Long.parseLong(chatId));
        if (chatEntityOptional.isEmpty()) {
            log.warn("Chat not found");
            return;
        }
        // Получаем данные отправителя из БД
        Optional<UserEntity> senderOpt = usersRepository.findById(Long.parseLong(senderId));
        if (senderOpt.isEmpty()) {
            log.warn("Отправитель не найден: {}", senderId);
            conn.send("{\"error\":\"Sender not found\"}");
            return;
        }
        UserEntity sender = senderOpt.get();

        ChatEntity chatEntity = chatEntityOptional.get();

        List<ChatMember> toMembers = chatMemberRepository.findByChatId(chatEntity.getId()).stream().filter(chatMember -> !chatMember.getUser().getId().equals(sender.getId())).toList();

        for (ChatMember member : toMembers) {
            // Отправляем получателю, если он онлайн
            String recipientId = String.valueOf(member.getUser().getId().longValue());

            WebSocket recipientSocket = userConnections.get(recipientId);
            if (recipientSocket != null && recipientSocket.isOpen()) {
                JSONObject msg = getMessageToSend(text, sender, member.getChat().getId(), messageType, JsonParser.jsonToFile(args.getJSONObject("file-info").toString()));
                recipientSocket.send(msg.toString());
                log.info("Сообщение от {} к {}: {}", senderId, recipientId, text);
            } else {
                log.info("Получатель {} офлайн. Сообщение сохранено.", recipientId);
            }

            MessageEntity entity = new MessageEntity();
            entity.setChat(member.getChat());
            entity.setType(MessageEntity.MessageType.valueOf(messageType));
            entity.setSender(sender);
            entity.setFileInfo(JsonParser.jsonToFile(args.getJSONObject("file-info").toString()));
            entity.setCreatedAt(LocalDateTime.now());
            entity.setContent(text);

            messageRepos.save(entity);
        }
    }

    private JSONObject getMessageToSend(String text, UserEntity sender, Long chatId, String type, File file) {

        JSONObject messageToSend = new JSONObject();
        messageToSend.put("op", "message");

        JSONObject messageArgs = new JSONObject();
        messageArgs.put("id", System.currentTimeMillis());
        messageArgs.put("content", text);
        messageArgs.put("timestamp", System.currentTimeMillis());
        messageArgs.put("type", type);
        messageArgs.put("chatId", chatId);

        // Добавляем данные отправителя
        JSONObject from = new JSONObject();
        from.put("id", sender.getId());
        from.put("userName", sender.getName());
        from.put("bio", sender.getBio());
        from.put("picture-url", sender.getAvatar());
        messageArgs.put("from", from);

        JSONObject fileInfo = new JSONObject();
        fileInfo.put("id", file.getId());
        fileInfo.put("name", file.getName());
        from.put("url", file.getUrl());

        messageArgs.put("file-info", fileInfo);

        messageToSend.put("args", messageArgs);

        System.out.println(messageToSend);
        return messageToSend;
    }

    private void handleStatus(WebSocket conn, JSONObject json) {
        String userId = socketToUser.get(conn);
        if (userId == null) {
            conn.send("{\"error\":\"Not authenticated\"}");
            return;
        }

        JSONObject args = json.getJSONObject("args");
        boolean isOnline = args.getBoolean("isOnline");

        Optional<UserEntity> userEntity = usersRepository.findById(Long.parseLong(userId));
        if (userEntity.isEmpty()) {
            log.info("Пользователь не найден: {}", userId);
            return;
        }

        // Рассылаем статус всем, кто подписан на этого пользователя
        // В 1 на 1 можно рассылать контактам пользователя
        broadcastStatus(userId, userEntity.get().getName(), isOnline);

        log.info("Статус пользователя {}: {}", userId, isOnline ? "онлайн" : "офлайн");
    }

    private void broadcastStatus(String userId, String userName, boolean isOnline) {
        JSONObject statusMessage = new JSONObject();
        statusMessage.put("op", "status");

        JSONObject args = new JSONObject();
        args.put("isOnline", isOnline);

        JSONObject from = new JSONObject();
        from.put("id", userId);
        from.put("name", userName);
        args.put("from", from);

        statusMessage.put("args", args);

        // Рассылаем всем онлайн пользователям
        // В реальном приложении - только контактам этого пользователя
        for (Map.Entry<String, WebSocket> entry : userConnections.entrySet()) {
            if (!entry.getKey().equals(userId)) { // Не отправляем себе
                WebSocket socket = entry.getValue();
                if (socket.isOpen()) {
                    socket.send(statusMessage.toString());
                }
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String userId = socketToUser.remove(conn);
        if (userId != null) {
            userConnections.remove(userId);

            Optional<UserEntity> userEntity = usersRepository.findById(Long.parseLong(userId));
            if (userEntity.isPresent()) {
                broadcastStatus(userId, userEntity.get().getName(), false);
                log.info("Пользователь отключился: {}", userId);
            }
        }
        isOpen = false;
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (conn != null) {
            String userId = socketToUser.get(conn);
            log.error("WebSocket ошибка у пользователя {}: {}", userId, ex.getMessage());
        } else {
            log.error("WebSocket ошибка: {}", ex.getMessage());
        }
        isOpen = false;
    }

    @Override
    public void onStart() {
        log.info("Сервер запущен на порту {}", getPort());
    }
}