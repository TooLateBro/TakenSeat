package com.taken_seat.auth_service.domain.repository.user;

import com.taken_seat.auth_service.domain.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

    Page<User> findAllByDeletedAtIsNull(String q, String role, Pageable pageable);
}
