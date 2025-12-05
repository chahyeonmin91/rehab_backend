package com.rehab.domain.entity;

import com.rehab.domain.entity.enums.LoginType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.rehab.domain.entity.base.BaseEntity;
import com.rehab.domain.entity.enums.Gender;
import com.rehab.domain.entity.enums.UserRole;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true)
    private String email;

	@Column(name = "password")
	private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

	@Column(name = "age")
	private Integer age;

	@Column(name = "height")
	private Double height;

	@Column(name = "weight")
	private Double weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "current_streak")
    private Integer currentStreak;

    @Column(name = "max_streak")
    private Integer maxStreak;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "fcm_token")
    private String fcmToken;

	@Enumerated(EnumType.STRING)
	@Column(name = "login_type")
	private LoginType loginType;

	//소셜 로그인용 필드
	@Column(name = "provider")
	private String provider;  // "kakao"

	@Column(name = "provider_id", unique = true)
	private String providerId;   // 카카오의 회원 고유번호

	@Builder.Default
	@Column(name = "profile_completed")
	private Boolean profileCompleted = false;

	// 연관관계

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RehabPlan> rehabPlans = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExerciseLog> exerciseLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailySummary> dailySummaries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecoveryScore> recoveryScores = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Medication> medications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MedicationLog> medicationLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReportSnapshot> reportSnapshots = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AiInferenceLog> aiInferenceLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AuditLog> auditLogs = new ArrayList<>();

	public static User createEmailUser(String email, String encodedPassword) {
		return User.builder()
			.email(email)
			.password(encodedPassword)
			.loginType(LoginType.EMAIL)
			.role(UserRole.USER)
			.profileCompleted(false)
			.build();
	}

	public static User createKakaoUser(String providerId, String email,String nickname) {
		return User.builder()
			.provider("kakao")
			.providerId(providerId)
			.email(email)
			.username(nickname)
			.loginType(LoginType.KAKAO)
			.role(UserRole.USER)
			.profileCompleted(false)
			.build();
	}
	public void updateProfile(String username, Gender gender, Integer age, Double height, Double weight) {
		this.username = username;
		this.gender = gender;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.profileCompleted = true;
	}
}
