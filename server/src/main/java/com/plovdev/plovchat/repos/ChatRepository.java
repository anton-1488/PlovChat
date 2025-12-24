package com.plovdev.plovchat.repos;

import com.plovdev.plovchat.entities.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
}
