package com.plovdev.plovchat.models.utils;

import java.util.UUID;

public class CommonUtils {
    public static String generateId() {
        return String.valueOf(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE).substring(0,10);
    }
}