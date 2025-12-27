package com.plovdev.plovchat;

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Launcher {
    private static final Logger log = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        try {
            Path path = Path.of("downloads");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            log.error("Initializing err: ", e);
        }

        Application.launch(PlovChatApp.class, args);
    }
}