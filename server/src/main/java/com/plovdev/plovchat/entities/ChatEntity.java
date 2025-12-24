package com.plovdev.plovchat.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // Связь с участниками
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMember> members = new ArrayList<>();

    // Связь с сообщениями (опционально)
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageEntity> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (lastMessageAt == null) {
            lastMessageAt = creationDate;
        }
    }

    // Метод для получения количества участников
    public int getMemberCount() {
        return members != null ? members.size() : 0;
    }

    // Метод для получения активных участников
    public List<ChatMember> getActiveMembers() {
        if (members == null) return new ArrayList<>();
        return members.stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsActive()))
                .collect(java.util.stream.Collectors.toList());
    }
}