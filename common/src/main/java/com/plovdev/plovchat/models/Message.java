package com.plovdev.plovchat.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("chatId")
    private String chatId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private MessageType type;

    @JsonProperty("timestamp")
    private Long timesamp;


    @JsonProperty("from")
    private User from;

    public Message() {
    }

    public Message(String id, String chatId, String content, MessageType type, Long timesamp, User from) {
        this.id = id;
        this.chatId = chatId;
        this.content = content;
        this.type = type;
        this.timesamp = timesamp;
        this.from = from;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Long getTimesamp() {
        return timesamp;
    }

    public void setTimesamp(Long timesamp) {
        this.timesamp = timesamp;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }


    public enum MessageType {
        TEXT
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(chatId, message.chatId) && Objects.equals(content, message.content) && type == message.type && Objects.equals(timesamp, message.timesamp) && Objects.equals(from, message.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, content, type, timesamp, from);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", chatId='" + chatId + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", timesamp=" + timesamp +
                ", from=" + from +
                '}';
    }
}