package com.taken_seat.common_service.aop.vo;

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
