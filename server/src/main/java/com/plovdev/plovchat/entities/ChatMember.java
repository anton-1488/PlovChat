package com.plovdev.plovchat.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "chat_members",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"chat_id", "user_id"}
        ))
public class ChatMember {

    public enum Role {
        CREATOR,   // Создатель чата
        ADMIN,     // Администратор
        MEMBER,    // Участник
        BANNED     // Заблокирован
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.MEMBER;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "muted_until")
    private LocalDateTime mutedUntil;

    @Column(name = "nickname")
    private String nickname; // Имя в конкретном чате

    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
        if (lastSeen == null) {
            lastSeen = LocalDateTime.now();
        }
    }

    // Конструктор для создания участника
    public ChatMember(ChatEntity chat, UserEntity user, Role role) {
        this.chat = chat;
        this.user = user;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
    }

    // Проверка, является ли участник администратором
    public boolean isAdmin() {
        return role == Role.ADMIN || role == Role.CREATOR;
    }

    // Проверка, заблокирован ли участник
    public boolean isBanned() {
        return role == Role.BANNED;
    }

    // Проверка, замьючен ли участник
    public boolean isMuted() {
        return mutedUntil != null && mutedUntil.isAfter(LocalDateTime.now());
    }
}