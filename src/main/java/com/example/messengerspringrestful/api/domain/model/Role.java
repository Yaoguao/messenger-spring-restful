package com.example.messengerspringrestful.api.domain.model;

@Deprecated
public enum Role {

    USER("USER");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
