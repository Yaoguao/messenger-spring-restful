package com.example.messengerspringrestful.api.model;

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
