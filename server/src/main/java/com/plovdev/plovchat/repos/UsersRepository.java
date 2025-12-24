package com.plovdev.plovchat.repos;

import com.plovdev.plovchat.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByIdAndPassword(Long id, String password);
}