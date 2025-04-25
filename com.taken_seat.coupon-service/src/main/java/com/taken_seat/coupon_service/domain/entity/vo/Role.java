package com.taken_seat.coupon_service.domain.entity.vo;


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
