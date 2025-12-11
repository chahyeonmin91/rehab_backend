package com.rehab.service.userService;

import com.rehab.domain.entity.User;
import com.rehab.domain.repository.user.UserRepository;
import com.rehab.dto.user.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserProfileDto.ProfileResponse getMyProfile(User user) {
		return UserProfileDto.ProfileResponse.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.email(user.getEmail())
			.gender(user.getGender())
			.age(user.getAge())
			.height(user.getHeight())
			.weight(user.getWeight())
			.profileCompleted(user.getProfileCompleted())
			.build();
	}

	@Override
	public UserProfileDto.ProfileResponse updateMyProfile(User user, UserProfileDto.ProfileUpdateRequest request) {

		LocalDate birthDate = request.getBirthDate() != null
			? request.getBirthDate()
			: user.getBirthDate();

		Integer age = null;
		if (birthDate != null) {
			age = Period.between(birthDate, LocalDate.now()).getYears();
		} else {
			age = request.getAge();
		}
		user.updateProfile(
			request.getUsername(),
			request.getGender(),
			age,
			request.getHeight(),
			request.getWeight(),
			birthDate
		);

		userRepository.save(user);

		return getMyProfile(user);
	}

}
