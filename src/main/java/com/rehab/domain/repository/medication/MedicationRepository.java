package com.rehab.domain.repository.medication;

import com.rehab.domain.entity.Medication;
import com.rehab.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
	List<Medication> findByUser(User user);
}
