package com.rehab.domain.repository.diet;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rehab.domain.entity.Diet;

public interface DietRepository extends JpaRepository<Diet, Long> {
}
