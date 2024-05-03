package com.aroubeidis.cards.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.aroubeidis.cards.dto.CardDto;

@Repository
public interface CardRepository extends JpaRepository<CardDto, Long> {

	Page<CardDto> findAll(@NonNull final Pageable pageable);
}