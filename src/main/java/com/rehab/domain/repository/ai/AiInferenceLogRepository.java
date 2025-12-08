package com.rehab.domain.repository.ai;

import com.rehab.domain.entity.AiInferenceLog;
import com.rehab.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 추론 로그 Repository
 */
@Repository
public interface AiInferenceLogRepository extends JpaRepository<AiInferenceLog, Long> {

	/**
	 * 사용자별 AI 추론 로그 조회
	 */
	@Query("SELECT a FROM AiInferenceLog a WHERE a.user = :user ORDER BY a.createdAt DESC")
	List<AiInferenceLog> findByUser(@Param("user") User user, Pageable pageable);

	/**
	 * 사용자 및 모델 키로 AI 추론 로그 조회
	 */
	@Query("SELECT a FROM AiInferenceLog a WHERE a.user = :user AND a.modelKey = :modelKey ORDER BY a.createdAt DESC")
	List<AiInferenceLog> findByUserAndModelKey(
		@Param("user") User user,
		@Param("modelKey") String modelKey,
		Pageable pageable
	);
}
