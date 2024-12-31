package vn.khanhduc.shoppingbackendservice.mapper;

import vn.khanhduc.shoppingbackendservice.common.UserStatus;
import vn.khanhduc.shoppingbackendservice.dto.request.UserCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.UserCreationResponse;
import vn.khanhduc.shoppingbackendservice.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static User toUser(UserCreationRequest request) {
        return User.builder()
                .email(request.getEmail())
                .name(request.getUsername())
                .userType(UserStatus.UNVERIFIED)
                .build();
    }

    public static UserCreationResponse toUserCreatedResponse(User user) {
        return UserCreationResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .isEnabled(String.valueOf(user.getUserType()))
                .build();
    }
}
