package com.plovdev.plovchat.repos;

import com.plovdev.plovchat.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepos extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findAllByChatId(Long chatId);
}