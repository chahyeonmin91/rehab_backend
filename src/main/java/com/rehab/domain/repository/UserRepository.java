package com.rehab.domain.repository;


import com.rehab.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;



public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByProviderAndProviderId(String provider, String providerId);

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email); // signup duplicate check 용
}


