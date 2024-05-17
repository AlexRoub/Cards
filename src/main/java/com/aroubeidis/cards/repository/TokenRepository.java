package com.aroubeidis.cards.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aroubeidis.cards.entities.TokenEntity;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

	@Query(
			"select t from TokenEntity t inner join UserEntity u on t.user.id = u.id " + "where u.id = :id and (t.expired = false or t.revoked"
					+ " = "
			+ "false)")
	List<TokenEntity> findAllValidTokenByUser(Long id);

	Optional<TokenEntity> findByToken(String token);
}