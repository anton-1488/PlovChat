package com.plovdev.plovchat.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.plovchat.PlovChatApp;
import com.plovdev.plovchat.models.Chat;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.models.utils.JsonParser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class RestManager {
    private static final Logger log = LoggerFactory.getLogger(RestManager.class);
    private static final Preferences prefs = Preferences.userNodeForPackage(PlovChatApp.class);
    private static RestManager INSTANCE;
    private static final String BASE_URL = "http://localhost:8080/api/"; //http://217.26.27.252:8080/api/
    private final HttpClient client;

    private final Gson gson = new Gson();

    public static RestManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RestManager();
        }
        return INSTANCE;
    }

    private RestManager() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    public boolean checkUser(String id, String password) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("password", password);

            HttpResponse<String> response = sendRequest("checkUser", map);
            if (response.statusCode() == 200) {
                JSONObject object = new JSONObject(response.body());
                int code = object.getInt("code");
                if (code == 0) {
                    log.info("User {} authenticated successfully", id);
                    return true;
                } else {
                    log.warn("Authentication failed for user {}, code: {}, message: {}",
                            id, code, object.optString("message", "No error message"));
                    return false;
                }
            } else {
                log.error("Server returned error status: {} for user {}", response.statusCode(), id);
                return false;
            }
        } catch (Exception e) {
            log.error("User checking error for ID {}: {}", id, e.getMessage());
        }
        return false;
    }

    public User createUser(String name, String password) {
        try {
            JsonObject regObj = new JsonObject();
            regObj.addProperty("name", name);
            regObj.addProperty("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "createUser"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(regObj)))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "PlovChat/1.0")
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject object = new JSONObject(response.body());
                int code = object.getInt("code");
                if (code == 0) {
                    log.info("User {} creating successfully", name);
                    return JsonParser.jsonToUser(object.getJSONObject("data").toString());
                }
            }
        } catch (Exception e) {
            log.error("User creating error for ID {}: {}", name, e.getMessage());
        }
        return new User();
    }


    public Chat createChat(String chatUserId, String chatName) {
        try {
            JsonObject regObj = new JsonObject();
            regObj.addProperty("otherUserId", chatUserId);
            regObj.addProperty("chatName", chatName);
            regObj.addProperty("isGroup", false);
            regObj.addProperty("description", "");
            regObj.addProperty("pictureUrl", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6HF3J65TtE0sAUxDG0U86uixQSlIMmvxYxA&s");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "createChat"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(regObj)))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "PlovChat/1.0")
                    .header("User-Id", prefs.get("user-id", ""))
                    .header("User-Password", prefs.get("user-password", ""))
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject object = new JSONObject(response.body());
                int code = object.getInt("code");
                if (code == 0) {
                    log.info("Chat {} creating successfully", chatName);
                    return JsonParser.jsonToChat(object.getJSONObject("data").toString());
                }
            }
        } catch (Exception e) {
            log.error("Chat creating error for ID {}: {}", chatName, e.getMessage());
        }
        return new Chat();
    }


    private HttpResponse<String> sendRequest(String reqUrl, Map<String, String> values) {
        try {
            StringBuilder url = new StringBuilder(String.format("%s", BASE_URL + reqUrl));

            if (values != null && !values.isEmpty()) {
                url.append("?");

                for (String key : values.keySet()) {
                    String val = values.get(key);

                    String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
                    String encodedVal = URLEncoder.encode(val, StandardCharsets.UTF_8);

                    url.append(String.format("%s=%s&", encodedKey, encodedVal));
                }
                url.deleteCharAt(url.length() - 1);
            }

            System.out.println(url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "PlovChat/1.0")
                    .timeout(Duration.ofSeconds(20))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<Chat> getChats(String id, String password) {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "getUserChats"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "PlovChat/1.0")
                    .header("User-Password", password)
                    .header("User-Id", id)
                    .timeout(Duration.ofSeconds(20))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject object = new JSONObject(response.body());
                int code = object.getInt("code");
                if (code == 0) {
                    return JsonParser.jsonToChatList(response.body());
                }
            }
        } catch (Exception e) {
            log.error("Failed load user chats", e);
        }
        return new ArrayList<>();
    }


    public List<Message> loadMessages(String chatId, String userId, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "getMessages"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "PlovChat/1.0")
                    .header("User-Id", prefs.get("user-id", ""))
                    .header("User-Password", prefs.get("user-password", ""))
                    .header("Chat-Id", chatId)
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject object = new JSONObject(response.body());
                int code = object.getInt("code");
                if (code == 0) {
                    System.out.println(response.body());
                    return JsonParser.jsonToMessageList(response.body());
                }
            }
        } catch (Exception e) {
            log.error("Failed load user chats", e);
        }
        return new ArrayList<>();
    }

    public void close() {
        client.close();
    }
}