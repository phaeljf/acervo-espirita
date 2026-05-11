package com.acervo.acervoespirita.model.enums;

public enum UserRole {

    USER("Frequentador"),
    STAFF("Funcionário"),
    ADMIN("Diretoria");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}