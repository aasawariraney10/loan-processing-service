package com.loan.evaluation.enums;

public enum Status {

    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}