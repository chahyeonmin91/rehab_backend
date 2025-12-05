package com.rehab.domain.entity;

import com.rehab.domain.entity.base.BaseEntity;
import com.rehab.domain.entity.enums.ExerciseExperience;
import com.rehab.domain.entity.enums.PainArea;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "symptom_intake")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SymptomIntake extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "intake_id")
	private Long intakeId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	private PainArea painArea;

	@Column(name = "pain_level")
	private Integer painLevel;

	@Column(name = "goal")
	private String goal;

	@Enumerated(EnumType.STRING)
	private ExerciseExperience exerciseExperience;
}
