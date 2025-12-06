package com.rehab.domain.repository.exercise;

import com.rehab.domain.entity.Exercise;
import com.rehab.domain.entity.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Exercise Repository
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

	/**
	 * 운동 상세 조회 (이미지, 미디어 함께 로드)
	 */
	@Query("SELECT DISTINCT e FROM Exercise e " +
		"LEFT JOIN FETCH e.exerciseImages " +
		"LEFT JOIN FETCH e.exerciseMedias " +
		"WHERE e.exerciseId = :exerciseId")
	Optional<Exercise> findByIdWithDetails(@Param("exerciseId") Long exerciseId);

	/**
	 * 신체 부위로 운동 조회
	 */
	List<Exercise> findByBodyPart(String bodyPart);

	/**
	 * 난이도로 운동 조회
	 */
	List<Exercise> findByDifficulty(Difficulty difficulty);

	/**
	 * 신체 부위와 난이도로 운동 조회
	 */
	List<Exercise> findByBodyPartAndDifficulty(String bodyPart, Difficulty difficulty);
}
