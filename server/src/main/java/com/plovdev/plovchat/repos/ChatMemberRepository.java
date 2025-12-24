package com.plovdev.plovchat.repos;

import com.plovdev.plovchat.entities.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    // Найти участников чата
    List<ChatMember> findByChatId(Long chatId);

    // Найти чаты пользователя
    List<ChatMember> findByUserId(Long userId);

    // Найти конкретного участника
    Optional<ChatMember> findByChatIdAndUserId(Long chatId, Long userId);

    // Проверить, состоит ли пользователь в чате
    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    // Получить чаты пользователя с последним сообщением
    @Query("SELECT cm FROM ChatMember cm " +
            "LEFT JOIN FETCH cm.chat c " +
            "LEFT JOIN MessageEntity m ON m.chat.id = c.id AND m.createdAt = " +
            "(SELECT MAX(m2.createdAt) FROM MessageEntity m2 WHERE m2.chat.id = c.id) " +
            "WHERE cm.user.id = :userId AND cm.isActive = true " +
            "ORDER BY c.lastMessageAt DESC")
    List<ChatMember> findUserChatsWithLastMessage(@Param("userId") Long userId);

    // Найти активных участников чата
    @Query("SELECT cm FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.isActive = true")
    List<ChatMember> findActiveMembersByChatId(@Param("chatId") Long chatId);

    // Найти администраторов чата
    List<ChatMember> findByChatIdAndRoleIn(Long chatId, List<ChatMember.Role> roles);
}