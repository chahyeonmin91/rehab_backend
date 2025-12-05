package com.rehab.service.userService;

import com.rehab.domain.entity.User;
import com.rehab.domain.repository.UserRepository;
import com.rehab.dto.user.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

		user.updateProfile(
			request.getUsername(),
			request.getGender(),
			request.getAge(),
			request.getHeight(),
			request.getWeight()
		);

		// JPA dirty checking 으로 자동 반영, 그래도 명시적으로 save 호출해도 OK
		userRepository.save(user);

		return getMyProfile(user);
	}
}
