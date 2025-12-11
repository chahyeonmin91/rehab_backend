package com.rehab.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehab.apiPayload.ApiResponse;
import com.rehab.domain.entity.PlanItem;
import com.rehab.domain.repository.medication.MedicationRepository;
import com.rehab.domain.repository.plan.PlanItemRepository;
import com.rehab.domain.repository.user.UserRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
public class DebugController {

	private final PlanItemRepository planItemRepository;
	private final MedicationRepository medicationRepository;
	private final UserRepository userRepository;
	private final EntityManager entityManager;

	@GetMapping("/direct-query")
	public ApiResponse<Map<String, Object>> directQuery() {
		Map<String, Object> result = new HashMap<>();

		try {
			// 1. Native Query로 직접 조회
			String sql = "SELECT * FROM plan_item WHERE plan_item_id = 1";
			Object rawResult = entityManager.createNativeQuery(sql).getSingleResult();
			result.put("nativeQuerySuccess", true);
			result.put("nativeQueryResult", rawResult.toString());
		} catch (Exception e) {
			result.put("nativeQueryError", e.getMessage());
		}

		try {
			// 2. JPA로 조회
			Optional<PlanItem> planItem = planItemRepository.findById(1L);
			result.put("jpaQuerySuccess", planItem.isPresent());
			if (planItem.isPresent()) {
				result.put("jpaResult", "PlanItem ID: " + planItem.get().getPlanItemId());
			}
		} catch (Exception e) {
			result.put("jpaQueryError", e.getMessage());
		}

		try {
			// 3. count 쿼리
			long count = planItemRepository.count();
			result.put("totalCount", count);
		} catch (Exception e) {
			result.put("countError", e.getMessage());
		}

		return ApiResponse.onSuccess(result);
	}

	@GetMapping("/connection-info")
	public ApiResponse<Map<String, Object>> connectionInfo() {
		Map<String, Object> result = new HashMap<>();

		try {
			String sql = "SELECT DATABASE(), USER(), @@time_zone, @@sql_mode";
			Object[] info = (Object[]) entityManager.createNativeQuery(sql).getSingleResult();
			result.put("database", info[0]);
			result.put("user", info[1]);
			result.put("timezone", info[2]);
			result.put("sqlMode", info[3]);
		} catch (Exception e) {
			result.put("error", e.getMessage());
		}

		return ApiResponse.onSuccess(result);
	}
}
