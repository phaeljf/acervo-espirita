package com.acervo.acervoespirita.model.enums;

public enum LoanStatus {

    OPEN("Aberto"),
    CLOSED("Fechado"),
    OVERDUE("Atrasado");

    private final String description;

    LoanStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}