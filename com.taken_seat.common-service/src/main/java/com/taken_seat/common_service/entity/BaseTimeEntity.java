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
	protected LocalDateTime createdAt;

	@Column(updatable = false, nullable = false)
	protected UUID createdBy;

	@LastModifiedDate
	protected LocalDateTime updatedAt;

	@Column
	protected UUID updatedBy;

	@Column
	protected LocalDateTime deletedAt;

	@Column
	protected UUID deletedBy;

	public void delete(UUID deleteBy) {
		this.deletedBy = deleteBy;
		this.deletedAt = LocalDateTime.now();
	}

	public void prePersist(UUID createdBy) {
		this.createdAt = LocalDateTime.now();
		this.createdBy = createdBy;
		this.updatedAt = null;
		this.updatedBy = null;
	}

	public void preUpdate(UUID updatedBy) {
		this.updatedAt = LocalDateTime.now();
		this.updatedBy = updatedBy;
	}

	public void rollbackDelete(UUID updatedBy) {
		this.deletedAt = null;
		this.deletedBy = null;
		this.updatedAt = LocalDateTime.now();
		this.updatedBy = updatedBy;
	}
}
