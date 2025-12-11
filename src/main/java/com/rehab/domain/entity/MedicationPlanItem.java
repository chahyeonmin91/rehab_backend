package com.rehab.domain.entity;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rehab.domain.entity.base.BaseEntity;
import com.rehab.domain.entity.enums.PlanItemStatus;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medication_plan_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MedicationPlanItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "medication_plan_item_id")
	private Long medicationPlanItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rehab_plan_id", nullable = false)
	private RehabPlan rehabPlan;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medication_id", nullable = false)
	private Medication medication;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PlanItemStatus status;

	@Column(name = "order_index")
	private Integer orderIndex;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "recommendation_reason", columnDefinition = "JSON")
	private String recommendationReason;
}
