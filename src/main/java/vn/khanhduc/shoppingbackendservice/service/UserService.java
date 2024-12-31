package vn.khanhduc.shoppingbackendservice.service;

import vn.khanhduc.shoppingbackendservice.dto.request.UserCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.UserCreationResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.UserProfileResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.VerifyOtpResponse;

public interface UserService {
    UserCreationResponse createUser(UserCreationRequest request);
    VerifyOtpResponse verifyUser(String email, int otp);
    UserProfileResponse getUserProfile();
}
