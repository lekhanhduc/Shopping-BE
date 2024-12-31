package vn.khanhduc.shoppingbackendservice.service;

import java.time.Duration;

public interface RedisService {

    void storeRefreshToken(String email, String refreshToken);

    void storeRefreshToken(String email, String refreshToken, Duration duration);

    String getRefreshToken(String email);

    void deleteRefreshToken(String email);

    void saveOtp(String email, String otp);

    String getOtp(String email);

    void deleteOtp(String email);
}
