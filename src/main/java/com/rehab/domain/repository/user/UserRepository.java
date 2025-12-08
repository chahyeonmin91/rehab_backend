package com.rehab.domain.repository.user;


import com.rehab.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;



public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByProviderAndProviderId(String provider, String providerId);

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email); // signup duplicate check 용

	/**
	 * 사용자 ID로 조회 (운동 로그 포함)
	 */
	@Query("SELECT DISTINCT u FROM User u " +
		"LEFT JOIN FETCH u.exerciseLogs el " +
		"LEFT JOIN FETCH el.planItem pi " +
		"LEFT JOIN FETCH pi.exercise " +
		"WHERE u.userId = :userId")
	Optional<User> findByIdWithExerciseLogs(@Param("userId") Long userId);
}


