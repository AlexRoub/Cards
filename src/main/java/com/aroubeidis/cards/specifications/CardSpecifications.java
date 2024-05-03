package com.aroubeidis.cards.specifications;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.aroubeidis.cards.dto.CardDto;
import com.aroubeidis.cards.model.Status;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;

public class CardSpecifications {

	public static Specification<CardDto> nameLike(final String name) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	public static Specification<CardDto> colorEquals(final String color) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("color")), color.toLowerCase());
	}

	public static Specification<CardDto> statusEquals(final Status status) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
	}

	public static Specification<CardDto> creationDateEquals(final LocalDate creationDate) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("creationDate"), creationDate);
	}

	public static Specification<CardDto> userIdEquals(final Long userId) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user")
			.get("id"), userId);
	}

	public static Specification<CardDto> createSpecification(final String name,
		final String color,
		final String status,
		final LocalDate creationDate,
		final Long userId) {

		return (root, query, criteriaBuilder) -> {
			final List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(name)) {
				predicates.add(nameLike(name).toPredicate(root, query, criteriaBuilder));
			}
			if (StringUtils.isNotBlank(color)) {
				predicates.add(colorEquals(color).toPredicate(root, query, criteriaBuilder));
			}
			if (status != null) {
				predicates.add(statusEquals(Status.valueOf(status)).toPredicate(root, query, criteriaBuilder));
			}
			if (creationDate != null) {
				predicates.add(creationDateEquals(creationDate).toPredicate(root, query, criteriaBuilder));
			}
			if (userId != null) {
				predicates.add(userIdEquals(userId).toPredicate(root, query, criteriaBuilder));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}