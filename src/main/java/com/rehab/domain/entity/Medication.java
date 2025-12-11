package com.rehab.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.rehab.domain.entity.base.BaseEntity;
import com.rehab.domain.entity.enums.MedicationStatus;

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
@Table(name = "medication")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Medication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long medicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "dose")
    private String dose;

    @Column(name = "route")
    private String route;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MedicationStatus status;

    // 연관관계
    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MediSchedule> mediSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MedicationLog> medicationLogs = new ArrayList<>();

	@OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MedicationPlanItem> medicationPlanItems = new ArrayList<>();
}
