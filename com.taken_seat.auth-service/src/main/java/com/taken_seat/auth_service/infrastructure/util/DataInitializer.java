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

    // 고정된 created_by UUID
    private static final UUID FIXED_CREATED_BY = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-000000000001");

    @Override
    public void run(String... args) throws Exception {
        // 역할(Role)의 샘플 데이터
        String[] roles = {"MANAGER", "CUSTOMER", "PRODUCER", "ADMIN"};

        // 100개의 사용자 데이터를 동적으로 삽입 (하지만 예측 가능한 값으로)
        for (int i = 0; i < 100; i++) {
            // 예측 가능한 ID 생성 (인덱스 기반)
            String uuidString = String.format("abcdef01-2345-6789-abcd-%012d", i);
            UUID id = UUID.fromString(uuidString);

            String username = "user" + (i + 1);
            String email = "user" + (i + 1) + "@example.com";
            String phone = String.format("010-1234-%04d", i);
            String password = bCryptPasswordEncoder.encode("password" + (i + 1) + "!");
            String role = roles[i % roles.length];
            UUID createdBy = FIXED_CREATED_BY; // 항상 고정된 UUID 사용

            // SQL 쿼리
            String insertSql = "INSERT INTO p_user (id, username, email, phone, password, role, created_at, updated_at, created_by) " +
                    "VALUES (UUID_TO_BIN(?), ?, ?, ?, ?, ?, NOW(), NOW(), UUID_TO_BIN(?))";

            // 데이터 삽입
            jdbcTemplate.update(insertSql, id.toString(), username, email, phone, password, role, createdBy.toString());
            System.out.println("Inserted user: " + username + " (ID: " + id + ")");
        }
    }
}