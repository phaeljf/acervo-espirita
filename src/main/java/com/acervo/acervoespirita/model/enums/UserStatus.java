package com.acervo.acervoespirita.model.enums;

public enum UserStatus {

    ACTIVE("Ativo"),
    INACTIVE("Inativo");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}