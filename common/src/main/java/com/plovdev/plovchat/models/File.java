package com.plovdev.plovchat.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class File implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    public File() {
    }

    public File(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(id, file.id) && Objects.equals(name, file.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "File{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}