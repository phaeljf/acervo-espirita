package com.acervo.acervoespirita.model.enums;

public enum BookCopyStatus {

    AVAILABLE("Disponível"),
    LOANED("Emprestado"),
    LOST("Perdido"),
    DAMAGED("Danificado"),
    INACTIVE("Inativo");

    private final String description;

    BookCopyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}