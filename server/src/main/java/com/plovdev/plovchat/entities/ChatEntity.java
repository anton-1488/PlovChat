package com.plovdev.plovchat.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chats")
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Лучше использовать Long для ID

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "picture_url")
    private String avatar;  // Используйте String вместо URI

    @Column(name = "is_group")
    private boolean isGroup = false;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (lastMessageAt == null) {
            lastMessageAt = creationDate;
        }
    }
}