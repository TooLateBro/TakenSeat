package com.taken_seat.auth_service.domain.vo;


public enum Role {
    CUSTOMER, MANAGER, ADMIN, PRODUCER;

    public boolean isCustomer() {
        return this == CUSTOMER;
    }

    public boolean isManager() {
        return this == MANAGER;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isProducer() {
        return this == PRODUCER;
    }
}
