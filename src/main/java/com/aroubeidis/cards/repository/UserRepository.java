package com.aroubeidis.cards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aroubeidis.cards.dto.UserDto;

public interface UserRepository extends JpaRepository<UserDto, Long> {

	Optional<UserDto> findByEmail(final String email);
}
