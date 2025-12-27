package com.plovdev.plovchat.rest.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plovdev.plovchat.dto.CreateChatRequest;
import com.plovdev.plovchat.dto.RegisterRequest;
import com.plovdev.plovchat.entities.ChatEntity;
import com.plovdev.plovchat.entities.ChatMember;
import com.plovdev.plovchat.entities.MessageEntity;
import com.plovdev.plovchat.entities.UserEntity;
import com.plovdev.plovchat.models.File;
import com.plovdev.plovchat.repos.ChatMemberRepository;
import com.plovdev.plovchat.repos.ChatRepository;
import com.plovdev.plovchat.repos.MessageRepos;
import com.plovdev.plovchat.repos.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final UsersRepository usersRepository;
    private final MessageRepos messageRepos;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final Gson gson;

    @GetMapping("/checkUser")
    public ResponseEntity<String> checkUser(@RequestParam("id") Long userId, @RequestParam("password") String password) {
        try {
            UserEntity entity = usersRepository.findByIdAndPassword(userId, password);
            System.err.println("Checking user");
            if (entity != null) {
                return ResponseEntity.ok(JsonPresets.simpleOk());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("User checking error: ", e);
            return ResponseEntity.internalServerError().body(JsonPresets.internalServerError());
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest request) {
        try {
            UserEntity entity = new UserEntity();

            entity.setName(request.getName());
            entity.setPassword(request.getPassword());
            entity.setBio("");
            entity.setAvatar("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6HF3J65TtE0sAUxDG0U86uixQSlIMmvxYxA&s");

            UserEntity user = usersRepository.save(entity);

            JsonObject userObject = new JsonObject();
            userObject.addProperty("code", 0);
            userObject.addProperty("msg", "User created");

            JsonObject userData = new JsonObject();
            userData.addProperty("id", user.getId());
            userData.addProperty("name", user.getName());
            userData.addProperty("bio", user.getBio());
            userData.addProperty("picture-url", user.getAvatar());

            userObject.add("data", userData);

            return ResponseEntity.ok(gson.toJson(userObject));
        } catch (Exception e) {
            log.error("User checking error: ", e);
            return ResponseEntity.internalServerError().body(JsonPresets.internalServerError());
        }
    }

    @GetMapping("/getUserChats")
    public ResponseEntity<String> getChats(@RequestHeader("User-Id") Long id, @RequestHeader("User-Password") String password) {
        try {
            UserEntity entity = usersRepository.findByIdAndPassword(id, password);
            if (entity != null) {
                List<ChatEntity> messagesList = chatMemberRepository.findByUserId(id).stream().map(ChatMember::getChat).toList();
                JsonObject answer = new JsonObject();
                answer.addProperty("code", 0);
                answer.addProperty("msg", "Chats loaded");

                JsonArray chats = new JsonArray();

                for (ChatEntity chat : messagesList) {
                    JsonObject messageNode = getJsonChat(chat);
                    chats.add(messageNode);
                }

                answer.add("data", chats);
                return ResponseEntity.ok(gson.toJson(answer));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("User checking error: ", e);
            return ResponseEntity.internalServerError().body(JsonPresets.internalServerError());
        }
    }


    @GetMapping("/getMessages")
    public ResponseEntity<String> getMessages(@RequestHeader("Chat-Id") Long chatId, @RequestHeader("User-Id") Long id, @RequestHeader("User-Password") String password) {
        try {
            UserEntity entity = usersRepository.findByIdAndPassword(id, password);
            if (entity != null) {
                List<MessageEntity> messagesList = messageRepos.findAllByChatId(chatId);
                JsonObject answer = new JsonObject();
                answer.addProperty("code", 0);
                answer.addProperty("msg", "Messages loaded");

                JsonArray messages = new JsonArray();

                for (MessageEntity message : messagesList) {
                    JsonObject messageNode = getJsonObject(message);
                    messages.add(messageNode);
                }

                answer.add("data", messages);
                return ResponseEntity.ok(gson.toJson(answer));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("User checking error: ", e);
            return ResponseEntity.internalServerError().body(JsonPresets.internalServerError());
        }
    }

    private JsonObject getJsonObject(MessageEntity message) {
        JsonObject messageNode = new JsonObject();
        messageNode.addProperty("id", message.getId());
        messageNode.addProperty("chatId", message.getChat().getId());
        messageNode.addProperty("content", message.getContent());
        messageNode.addProperty("type", message.getType() != null ? message.getType().name() : "TEXT");
        messageNode.addProperty("timestamp", message.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());

        // Информация об отправителе
        if (message.getSender() != null) {
            JsonObject senderNode = new JsonObject();
            senderNode.addProperty("id", message.getSender().getId());
            senderNode.addProperty("userName", message.getSender().getName());
            senderNode.addProperty("pictureUrl", message.getSender().getAvatar() != null ? message.getSender().getAvatar() : "");
            messageNode.add("from", senderNode);
        }
        File file = message.getFileInfo();
        if (file != null) {
            JsonObject fileInfo = new JsonObject();
            fileInfo.addProperty("id", file.getId());
            fileInfo.addProperty("name", file.getName());
            fileInfo.addProperty("url", file.getUrl());
            messageNode.add("file-info", fileInfo);
        }
        return messageNode;
    }


    private JsonObject getJsonChat(ChatEntity chat) {
        JsonObject chatNode = new JsonObject();
        chatNode.addProperty("id", chat.getId());
        chatNode.addProperty("name", chat.getName());
        chatNode.addProperty("description", chat.getDescription());
        chatNode.addProperty("picture-url", chat.getAvatar());
        chatNode.addProperty("isPrivate", !chat.isGroup());
        chatNode.addProperty("creationDate", chat.getCreationDate().toEpochSecond(ZoneOffset.UTC));

        return chatNode;
    }


    @PostMapping("/createChat")
    public ResponseEntity<String> createChat(
            @RequestHeader("User-Id") Long userId,
            @RequestHeader("User-Password") String password,
            @RequestBody CreateChatRequest request) {
        try {
            if (userId.equals(request.getOtherUserId())) {
                return ResponseEntity.badRequest().build();
            }

            log.info("Creating chat for user: {} with: {}", userId, request.getOtherUserId());

            // 1. Аутентификация
            UserEntity currentUser = usersRepository.findByIdAndPassword(userId, password);
            if (currentUser == null) {
                return ResponseEntity.badRequest().build();
            }
            // 2. Проверяем существование второго пользователя
            Optional<UserEntity> otherUserOpt = usersRepository.findById(request.getOtherUserId());
            if (otherUserOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserEntity otherUser = otherUserOpt.get();

            ChatEntity newChat = createPrivateChat(currentUser, otherUser, request);

            addChatMembers(newChat, currentUser, otherUser);

            JsonObject createdChat = new JsonObject();
            createdChat.addProperty("code", 0);
            createdChat.addProperty("msg", "Chat created");
            createdChat.add("data", getJsonChat(newChat));

            return ResponseEntity.ok(gson.toJson(createdChat));

        } catch (Exception e) {
            log.error("Error creating chat: ", e);
            return ResponseEntity.internalServerError().body(JsonPresets.internalServerError());
        }
    }

    private ChatEntity createPrivateChat(UserEntity user1, UserEntity user2, CreateChatRequest request) {
        ChatEntity chat = new ChatEntity();
        String chatName = request.getChatName();
        boolean isGroup = request.isGroup();

        // Если имя не указано, генерируем из имен пользователей
        if (chatName == null || chatName.trim().isEmpty()) {
            chatName = user1.getName() + " и " + user2.getName();
        }

        //chat.setId(Long.parseLong(String.valueOf(user1.getId()) + user2.getId()));
        chat.setName(chatName);
        chat.setGroup(isGroup);
        chat.setDescription(request.getDescription());
        chat.setAvatar(request.getPictureUrl());

        return chatRepository.save(chat);
    }

    private void addChatMembers(ChatEntity chat, UserEntity user1, UserEntity user2) {
        // Первый пользователь (инициатор)
        ChatMember member1 = new ChatMember();
        member1.setChat(chat);
        member1.setUser(user1);
        member1.setRole(ChatMember.Role.MEMBER);
        member1.setJoinedAt(java.time.LocalDateTime.now());
        chatMemberRepository.save(member1);

        // Второй пользователь (собеседник)
        ChatMember member2 = new ChatMember();
        member2.setChat(chat);
        member2.setUser(user2);
        member2.setRole(ChatMember.Role.MEMBER);
        member2.setJoinedAt(java.time.LocalDateTime.now());
        chatMemberRepository.save(member2);
    }
}
