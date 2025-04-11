package com.taken_seat.auth_service.infrastructure.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 역할(Role)의 샘플 데이터
        String[] roles = {"MANAGER", "CUSTOMER", "PRODUCER", "ADMIN"};

        // created_by UUID 기준값 (임의로 3개의 UUID를 준비)
        UUID[] createdByUUIDs = {
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000001"),
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000002"),
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000003")
        };

        // 100개의 사용자 데이터를 동적으로 삽입
        for (int i = 0; i < 100; i++) {
            // 동적으로 데이터 생성
            UUID id = UUID.randomUUID(); // 고유 사용자 ID 생성
            String username = "user" + (i + 1); // "user1", "user2" 등
            String email = "user" + (i + 1) + "@example.com"; // "user1@example.com" 등
            String phone = String.format("010-1234-%04d", i); // "010-1234-0000", "010-1234-0001" 등
            String password = bCryptPasswordEncoder.encode("password" + (i + 1) + "!"); // "password1!", "password2!" 등
            String role = roles[i % roles.length]; // 역할은 순환적으로 설정 (MANAGER, CUSTOMER 등)
            UUID createdBy = createdByUUIDs[i % createdByUUIDs.length]; // 생성자는 순환적으로 설정

            // SQL 쿼리
            String insertSql = "INSERT INTO p_user (id, username, email, phone, password, role, created_at, updated_at, created_by) " +
                    "VALUES (UUID_TO_BIN(?), ?, ?, ?, ?, ?, NOW(), NOW(), UUID_TO_BIN(?))";

            // 데이터 삽입
            jdbcTemplate.update(insertSql, id.toString(), username, email, phone, password, role, createdBy.toString());
            System.out.println("Inserted user: " + username + " (ID: " + id + ")");
        }
    }
}