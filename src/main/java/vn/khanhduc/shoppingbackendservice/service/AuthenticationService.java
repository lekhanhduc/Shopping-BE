package vn.khanhduc.shoppingbackendservice.service;

import org.springframework.http.ResponseEntity;
import vn.khanhduc.shoppingbackendservice.dto.request.SignInRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.RefreshTokenResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.SignInResponse;

import java.text.ParseException;

public interface AuthenticationService {
    ResponseEntity<SignInResponse> signIn(SignInRequest request);
    ResponseEntity<RefreshTokenResponse> refreshToken(String refreshToken) throws ParseException;
}
