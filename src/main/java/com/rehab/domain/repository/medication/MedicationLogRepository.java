package com.rehab.domain.repository.medication;

import com.rehab.domain.entity.MedicationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> { }
