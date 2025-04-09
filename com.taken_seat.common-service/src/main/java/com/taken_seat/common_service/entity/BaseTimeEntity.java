package com.taken_seat.common_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(updatable = false, nullable = false)
	private UUID createdBy;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column
	private UUID updatedBy;

	@Column
	private LocalDateTime deletedAt;

	@Column
	private UUID deletedBy;

	public void delete(UUID deleteBy) {
		this.deletedBy = deleteBy;
		this.deletedAt = LocalDateTime.now();
	}

	public void prePersist(UUID createdBy) {
		if (createdAt == null && createdBy == null) {
			createdAt = LocalDateTime.now();
			this.createdBy = createdBy;
		}

		updatedAt = null;
		updatedBy = null;
	}

	public void preUpdate(UUID updatedBy) {
		updatedAt = LocalDateTime.now();
		this.updatedBy = updatedBy;
	}

	public void rollbackDelete(UUID updatedBy) {
		this.deletedAt = null;
		this.deletedBy = null;
		this.updatedAt = LocalDateTime.now();
		this.updatedBy = updatedBy;

	}

}
