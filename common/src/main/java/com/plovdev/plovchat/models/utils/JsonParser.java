package com.plovdev.plovchat.models.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.JsonArray;
import com.plovdev.plovchat.models.Chat;
import com.plovdev.plovchat.models.File;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private static final Logger log = LoggerFactory.getLogger(JsonParser.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule()); // Для работы с LocalDateTime
    }

    public static List<Chat> jsonToChatList(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            validate(root);

            // Получаем массив чатов
            JsonNode chatsNode = root.path("data");
            if (chatsNode.isMissingNode() || !chatsNode.isArray()) {
                log.warn("No 'chats' array found in response");
                return new ArrayList<>();
            }

            // Парсим массив
            return mapper.readValue(
                    chatsNode.toString(),
                    new TypeReference<>() {}
            );

        } catch (Exception e) {
            log.error("Ошибка парсинга списка чатов: {}", e.getMessage());
            log.debug("JSON был: {}", json);
        }
        return new ArrayList<>();
    }


    public static List<Message> jsonToMessageList(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            validate(root);

            // Получаем массив чатов
            JsonNode messagesNode = root.path("data");
            if (messagesNode.isMissingNode() || !messagesNode.isArray()) {
                log.warn("No 'messages' array found in response");
                return new ArrayList<>();
            }

            // Парсим массив
            return mapper.readValue(
                    messagesNode.toString(),
                    new TypeReference<>() {}
            );

        } catch (Exception e) {
            log.error("Ошибка парсинга списка сообщений: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public static Message jsonToMessage(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            // Парсим массив
            return mapper.readValue(
                    root.toString(),
                    new TypeReference<>() {}
            );

        } catch (Exception e) {
            log.error("Ошибка парсинга сообщения: {}", e.getMessage());
        }
        return new Message();
    }

    public static File jsonToFile(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            // Парсим массив
            return mapper.readValue(
                    root.toString(),
                    new TypeReference<>() {}
            );

        } catch (Exception e) {
            log.error("Ошибка парсинга файла: {}", e.getMessage());
        }
        return new File();
    }

    public static Chat jsonToChat(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            // Парсим массив
            return mapper.readValue(
                    root.toString(),
                    new TypeReference<>() {}
            );

        } catch (Exception e) {
            log.error("Ошибка парсинга чата: {}", e.getMessage());
        }
        return new Chat();
    }


    public static User jsonToUser(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            // Парсим массив
            return mapper.readValue(
                    root.toString(),
                    new TypeReference<>() {}
            );

        } catch (Exception e) {
            log.error("Ошибка парсинга пользователя: {}", e.getMessage());
        }
        return new User();
    }

    private static void validate(JsonNode root) {
        // Проверяем код ответа
        int code = root.path("code").asInt(-1);
        if (code != 0) {
            log.warn("Server returned non-zero code: {}", code);
        }
    }
}