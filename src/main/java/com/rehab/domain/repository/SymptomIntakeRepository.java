package com.rehab.domain.repository;

import com.rehab.domain.entity.SymptomIntake;
import com.rehab.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymptomIntakeRepository extends JpaRepository<SymptomIntake, Long> {

	Optional<SymptomIntake> findByUser(User user);
}
