package com.aroubeidis.cards.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.aroubeidis.cards.dto.CardDto;

@Repository
public interface CardRepository extends JpaRepository<CardDto, Long>, JpaSpecificationExecutor<CardDto> {

	Page<CardDto> findAll(@NonNull Specification<CardDto> spec, @NonNull final Pageable pageable);
}