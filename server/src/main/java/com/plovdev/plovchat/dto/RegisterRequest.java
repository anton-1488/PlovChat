package com.plovdev.plovchat.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String password;
}