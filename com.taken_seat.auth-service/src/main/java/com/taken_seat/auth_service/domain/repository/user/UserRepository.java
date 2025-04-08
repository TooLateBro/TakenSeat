package com.taken_seat.auth_service.domain.repository.user;

import com.taken_seat.auth_service.domain.entity.user.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);
}
