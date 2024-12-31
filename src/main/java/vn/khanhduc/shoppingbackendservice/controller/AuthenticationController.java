package vn.khanhduc.shoppingbackendservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.khanhduc.shoppingbackendservice.dto.request.SignInRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.RefreshTokenResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.SignInResponse;
import vn.khanhduc.shoppingbackendservice.exception.AppException;
import vn.khanhduc.shoppingbackendservice.exception.ErrorCode;
import vn.khanhduc.shoppingbackendservice.service.AuthenticationService;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-in")
    ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @PostMapping("/refresh-token")
    ResponseEntity<RefreshTokenResponse> refreshToken(@CookieValue(value = "refresh_token") String refreshToken) {
        try {
            return authenticationService.refreshToken(refreshToken);
        } catch (ParseException e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

}
