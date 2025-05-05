package com.taken_seat.auth_service.domain.repository.user;

import com.taken_seat.auth_service.domain.entity.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findByIdAndDeletedAtIsNull(UUID userId);

    List<UUID> findAllIdsByDeletedAtIsNull();
}
