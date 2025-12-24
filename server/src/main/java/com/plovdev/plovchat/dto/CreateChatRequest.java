package com.plovdev.plovchat.dto;

import lombok.Data;

@Data
public class CreateChatRequest {
    private Long otherUserId;     // ID собеседника
    private String chatName;      // Название чата (опционально)
    private boolean isGroup = false;  // Групповой ли чат
    private String description;   // Описание (для групповых чатов)
    private String pictureUrl;
}