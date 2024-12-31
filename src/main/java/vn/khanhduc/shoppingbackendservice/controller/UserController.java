package vn.khanhduc.shoppingbackendservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.khanhduc.shoppingbackendservice.dto.http.ResponseData;
import vn.khanhduc.shoppingbackendservice.dto.request.UserCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.UserCreationResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.UserProfileResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.VerifyOtpResponse;
import vn.khanhduc.shoppingbackendservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    ResponseData<UserCreationResponse> create(@RequestBody @Valid UserCreationRequest request) {
        var result = userService.createUser(request);

        return ResponseData.<UserCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Created Successfully")
                .data(result)
                .build();
    }

    @GetMapping("/users/confirm-email")
    ResponseData<VerifyOtpResponse> verifyOtp(@RequestParam String email,
                                              @RequestParam String secretCode) {
        var result = userService.verifyUser(email, Integer.parseInt(secretCode));

        return ResponseData.<VerifyOtpResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Created Successfully")
                .data(result)
                .build();
    }

    @GetMapping("/profile/user")
    ResponseData<UserProfileResponse> getUserProfile() {
        var result = userService.getUserProfile();

        return ResponseData.<UserProfileResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("User Profile")
                .data(result)
                .build();
    }

}
