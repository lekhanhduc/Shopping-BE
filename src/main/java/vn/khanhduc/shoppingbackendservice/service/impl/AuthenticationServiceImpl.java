package vn.khanhduc.shoppingbackendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vn.khanhduc.shoppingbackendservice.dto.request.SignInRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.RefreshTokenResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.SignInResponse;
import vn.khanhduc.shoppingbackendservice.entity.User;
import vn.khanhduc.shoppingbackendservice.exception.AppException;
import vn.khanhduc.shoppingbackendservice.exception.ErrorCode;
import vn.khanhduc.shoppingbackendservice.repository.UserRepository;
import vn.khanhduc.shoppingbackendservice.service.AuthenticationService;
import vn.khanhduc.shoppingbackendservice.service.JwtService;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<SignInResponse> signIn(SignInRequest request) {
        log.info("Authentication....");
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(Duration.ofDays(14))
                .domain("localhost")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("Authentication Success");
        return ResponseEntity.ok().headers(headers)
                .body(SignInResponse.builder()
                        .userId(user.getId())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
    }

    @Override
    public ResponseEntity<RefreshTokenResponse> refreshToken(String refreshToken) throws ParseException {
        log.info("RefreshToken Start....");

        String email = jwtService.extractEmail(refreshToken);
        Date expiryDate = jwtService.extractExpiryTime(refreshToken);

        User user = userRepository.findByEmailAndRefreshToken(email, refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_INVALID));

        if(jwtService.verifyToken(refreshToken, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            long oneDayInMillis = 24 * 60 * 60 * 1000;
            if(expiryDate.getTime() - new Date().getTime() < oneDayInMillis) {
                var newRefreshToken  = jwtService.generateRefreshToken(user);
                user.setRefreshToken(newRefreshToken);
                userRepository.save(user);

                ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                        .httpOnly(true)
                        .path("/")
                        .secure(false)
                        .maxAge(Duration.ofDays(14))
                        .domain("localhost")
                        .build();

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

                return ResponseEntity.ok().headers(headers)
                        .body(RefreshTokenResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(newRefreshToken)
                                .build());
            }
            return ResponseEntity.ok()
                    .body(RefreshTokenResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build());
        }
        throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
    }

}
