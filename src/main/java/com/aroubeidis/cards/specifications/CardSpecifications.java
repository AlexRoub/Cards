package com.aroubeidis.cards.specifications;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.aroubeidis.cards.entities.CardEntity;
import com.aroubeidis.cards.model.RequestFilters;
import com.aroubeidis.cards.model.Status;
import com.google.common.collect.Lists;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CardSpecifications {

	public static Specification<CardEntity> createSpecification(final RequestFilters filters, final Long userId) {

		return (root, query, criteriaBuilder) -> getPredicate(filters, userId, root, query, criteriaBuilder);
	}

	private static Specification<CardEntity> nameLike(final String name) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	private static Specification<CardEntity> colorEquals(final String color) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("color")), color.toLowerCase());
	}

	private static Specification<CardEntity> statusEquals(final Status status) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
	}

	private static Specification<CardEntity> creationDateEquals(final LocalDate creationDate) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("creationDate"), creationDate);
	}

	private static Specification<CardEntity> userIdEquals(final Long userId) {

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user")
				.get("id"), userId);
	}

	private static Predicate getPredicate(final RequestFilters filters,
			final Long userId, final Root<CardEntity> root,
			final CriteriaQuery<?> query,
			final CriteriaBuilder criteriaBuilder) {

		final List<Predicate> predicates = Lists.newArrayList();

		if (StringUtils.isNotBlank(filters.getName())) {
			predicates.add(nameLike(filters.getName()).toPredicate(root, query, criteriaBuilder));
		}
		if (StringUtils.isNotBlank(filters.getColor())) {
			predicates.add(colorEquals(filters.getColor()).toPredicate(root, query, criteriaBuilder));
		}
		if (filters.getStatus() != null) {
			predicates.add(statusEquals(Status.valueOf(filters.getStatus())).toPredicate(root, query, criteriaBuilder));
		}
		if (filters.getCreationDate() != null) {
			predicates.add(creationDateEquals(filters.getCreationDate()).toPredicate(root, query, criteriaBuilder));
		}
		if (userId != null) {
			predicates.add(userIdEquals(userId).toPredicate(root, query, criteriaBuilder));
		}

		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}
}