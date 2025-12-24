package com.plovdev.plovchat.rest.controllers;

public class JsonPresets {
    public static String internalServerError() {
        return """
                {
                    "code": 500,
                    "msg": "Server error"
                }
                """;
    }

    public static String simpleOk() {
        return """
                {
                    "code": 0,
                    "msg": "User verified"
                }
                """;
    }

    public static String userNotFound() {
        return """
                {
                    "code": 300,
                    "msg": "User not found"
                }
                """;
    }
}