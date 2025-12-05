package com.rehab.service.userService;

import com.rehab.domain.entity.User;
import com.rehab.dto.user.UserProfileDto;

public interface UserService {

	UserProfileDto.ProfileResponse getMyProfile(User user);

	UserProfileDto.ProfileResponse updateMyProfile(User user, UserProfileDto.ProfileUpdateRequest request);
}

