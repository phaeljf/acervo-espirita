package com.acervo.acervoespirita.model.enums;

public enum BookStatus {

    ACTIVE("Ativo"),
    ARCHIVED("Arquivado"),
    DONATED("Doado"),
    INACTIVE("Inativo");

    private final String description;

    BookStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}