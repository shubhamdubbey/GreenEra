package com.green_era.user_service.utils;

public enum RoleEnum {
    GARDENER("Gardener"),
    CUSTOMER("Customer"),
    ADMIN("Admin");

    private final String role;

    RoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
