package com.aroubeidis.cards.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aroubeidis.cards.dto.TokenDto;

public interface TokenRepository extends JpaRepository<TokenDto, Long> {

	@Query(
		"select t from TokenDto t inner join UserDto u on t.user.id = u.id " + "where u.id = :id and (t.expired = false or t.revoked = "
			+ "false)")
	List<TokenDto> findAllValidTokenByUser(Long id);

	Optional<TokenDto> findByToken(String token);
}