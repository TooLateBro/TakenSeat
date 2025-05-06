package com.taken_seat.auth_service.domain.repository.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taken_seat.auth_service.domain.entity.user.User;

@Repository
public interface UserRepository {

	Optional<User> findByEmail(String email);

	User save(User user);

	Optional<User> findByIdAndDeletedAtIsNull(UUID userId);

	@Query("SELECT u.id FROM User u WHERE u.deletedAt IS NULL")
	List<UUID> findAllIdsByDeletedAtIsNull();
}
