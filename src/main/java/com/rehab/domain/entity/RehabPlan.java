package com.rehab.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rehab.domain.entity.base.BaseEntity;
import com.rehab.domain.entity.enums.RehabPlanStatus;

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
@Table(name = "rehab_plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RehabPlan extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rehab_plan_id")
	private Long rehabPlanId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "title")
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private RehabPlanStatus status;

	@Column(name = "start_date")
	private LocalDateTime startDate;

	@Column(name = "end_date")
	private LocalDateTime endDate;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "meta", columnDefinition = "JSON")
	private String meta;

	@Column(name = "generated_by")
	private String generatedBy;

	// 연관관계
	@OneToMany(mappedBy = "rehabPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PlanItem> planItems = new ArrayList<>();

	@OneToMany(mappedBy = "rehabPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MedicationPlanItem> medicationPlanItems = new ArrayList<>();

	@OneToMany(mappedBy = "rehabPlan", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<DietPlanItem> dietPlanItems = new ArrayList<>();
}
