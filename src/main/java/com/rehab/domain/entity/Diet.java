package com.rehab.domain.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.rehab.domain.entity.base.BaseEntity;
import com.rehab.domain.entity.enums.DietCategory;
import com.rehab.domain.entity.enums.EvidenceLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diet extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "diet_id")
	private Long dietId;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	private DietCategory category;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "nutrition_info", columnDefinition = "JSON")
	private String nutritionInfo;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "ingredients", columnDefinition = "JSON")
	private String ingredients;

	@Column(name = "instructions", columnDefinition = "TEXT")
	private String instructions;

	@Column(name = "contraindications", columnDefinition = "TEXT")
	private String contraindications;

	@Enumerated(EnumType.STRING)
	@Column(name = "evidence_level")
	private EvidenceLevel evidenceLevel;

	// 연관관계
	@OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<DietImage> dietImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<DietPlanItem> dietPlanItems = new ArrayList<>();
}
