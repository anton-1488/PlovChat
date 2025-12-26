package com.plovdev.plovchat.rest.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.plovchat.entities.UserEntity;
import com.plovdev.plovchat.models.utils.CommonUtils;
import com.plovdev.plovchat.repos.ChatMemberRepository;
import com.plovdev.plovchat.repos.ChatRepository;
import com.plovdev.plovchat.repos.MessageRepos;
import com.plovdev.plovchat.repos.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final String UPLOAD_DIR = "/uploaded/";
    private final UsersRepository usersRepository;
    private final MessageRepos messageRepos;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final Gson gson;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestHeader("User-Id") Long userId,
            @RequestHeader("User-Password") String password,
            @RequestHeader("File-Name") String fileName,
            @RequestBody byte[] file) {

        try {
            UserEntity currentUser = usersRepository.findByIdAndPassword(userId, password);
            if (currentUser == null) {
                return ResponseEntity.badRequest().build();
            }
            String fileId = CommonUtils.generateId();

            Files.write(Path.of(UPLOAD_DIR + fileId), file);

            JsonObject uploaded = new JsonObject();
            uploaded.addProperty("code", 0);
            uploaded.addProperty("msg", "File uploaded");

            JsonObject data = new JsonObject();
            data.addProperty("id", fileId);
            data.addProperty("name", fileName);
            uploaded.add("data", data);

            return ResponseEntity.ok(gson.toJson(uploaded));

        } catch (Exception e) {
            log.error("Error upload file: ", e);
            return ResponseEntity.internalServerError().body(JsonPresets.internalServerError());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<InputStream> download(
            @RequestHeader("User-Id") Long userId,
            @RequestHeader("User-Password") String password,
            @RequestHeader("File-Id") String fileId) {

        UserEntity currentUser = usersRepository.findByIdAndPassword(userId, password);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(new FileInputStream(UPLOAD_DIR + fileId));
        } catch (FileNotFoundException e) {
            log.error("File not found");
            return ResponseEntity.notFound().build();
        }
    }

    private void validate(String id, String passw) {

    }
}
