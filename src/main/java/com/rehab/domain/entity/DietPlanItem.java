package com.rehab.domain.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rehab.domain.entity.base.BaseEntity;
import com.rehab.domain.entity.enums.MealTime;
import com.rehab.domain.entity.enums.PlanItemStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diet_plan_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DietPlanItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "diet_plan_item_id")
	private Long dietPlanItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rehab_plan_id", nullable = false)
	private RehabPlan rehabPlan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diet_id", nullable = false)
	private Diet diet;

	@Enumerated(EnumType.STRING)
	@Column(name = "meal_time")
	private MealTime mealTime;

	@Column(name = "portion")
	private String portion;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PlanItemStatus status;

	@Column(name = "order_index")
	private Integer orderIndex;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "recommendation_reason", columnDefinition = "JSON")
	private String recommendationReason;

	// 연관관계
	@OneToMany(mappedBy = "dietPlanItem", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<DietLog> dietLogs = new ArrayList<>();
}
