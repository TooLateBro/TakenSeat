package com.taken_seat.review_service.domain.model;

import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_review_like")
public class ReviewLike extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false, length = 36)
	private UUID id;

	@Column(nullable = false)
	private UUID authorId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	public static ReviewLike create(Review review, UUID userId) {

		ReviewLike reviewLike = ReviewLike.builder()
			.authorId(userId)
			.review(review)
			.build();

		reviewLike.prePersist(userId);

		return reviewLike;
	}

	public boolean isDeleted() {
		return this.getDeletedAt() != null;
	}

	public void addReviewLike(UUID authorId) {
		validateOwner(authorId);
		this.deletedAt = null;
		this.preUpdate(authorId);

	}

	public void cancelReviewLike(UUID authorId) {
		validateOwner(authorId);
		this.delete(authorId);
		this.preUpdate(authorId);
	}

	private void validateOwner(UUID authorId) {
		if (!this.authorId.equals(authorId)) {
			throw new ReviewException(ResponseCode.FORBIDDEN_REVIEW_ACCESS);
		}
	}
}
