package com.rehab.domain.repository.symptomIntake;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.rehab.domain.entity.SymptomIntake;
import com.rehab.domain.entity.User;

/**
 * SymptomIntake Repository
 * 문진 정보 조회 및 저장
 */
@Repository
public interface SymptomIntakeRepository extends JpaRepository<SymptomIntake, Long> {

	/**
	 * 사용자의 가장 최근 문진 정보 조회
	 */
	@Query("SELECT si FROM SymptomIntake si WHERE si.user.userId = :userId ORDER BY si.createdAt DESC")
	Optional<SymptomIntake> findLatestByUserId(@Param("userId") Long userId);

	/**
	 * 사용자의 문진 정보 존재 여부 확인
	 */
	boolean existsByUser(User user);

	/**
	 * 사용자 ID로 문진 정보 존재 여부 확인
	 */
	@Query("SELECT CASE WHEN COUNT(si) > 0 THEN true ELSE false END FROM SymptomIntake si WHERE si.user.userId = :userId")
	boolean existsByUserId(@Param("userId") Long userId);

	/**
	 * 사용자의 모든 문진 이력 개수 조회
	 */
	@Query("SELECT COUNT(si) FROM SymptomIntake si WHERE si.user.userId = :userId")
	long countByUserId(@Param("userId") Long userId);

	Optional<SymptomIntake> findByUser(User user);
	List<SymptomIntake> findAllByUserOrderByCreatedAtDesc(User user);

}
