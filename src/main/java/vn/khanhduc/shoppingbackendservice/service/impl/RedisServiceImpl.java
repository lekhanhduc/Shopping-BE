package vn.khanhduc.shoppingbackendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.khanhduc.shoppingbackendservice.service.RedisService;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private static final long OTP_EXPIRE_TIME = 5 * 60;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void storeRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken);
    }

    @Override
    public void storeRefreshToken(String email, String refreshToken, Duration duration) {
        redisTemplate.opsForValue().set(email, refreshToken, duration);
    }

    @Override
    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    @Override
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }

    @Override
    public void saveOtp(String email, String otp) {
        redisTemplate.opsForValue().set(email, otp, OTP_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    @Override
    public String getOtp(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    @Override
    public void deleteOtp(String email) {
        redisTemplate.delete(email);
    }

}
