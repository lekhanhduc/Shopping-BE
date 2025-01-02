package vn.khanhduc.shoppingbackendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.khanhduc.shoppingbackendservice.common.GenerateOtp;
import vn.khanhduc.shoppingbackendservice.common.UserStatus;
import vn.khanhduc.shoppingbackendservice.common.UserType;
import vn.khanhduc.shoppingbackendservice.dto.event.EmailEvent;
import vn.khanhduc.shoppingbackendservice.dto.request.UserCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.UserCreationResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.UserProfileResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.VerifyOtpResponse;
import vn.khanhduc.shoppingbackendservice.entity.Role;
import vn.khanhduc.shoppingbackendservice.entity.User;
import vn.khanhduc.shoppingbackendservice.exception.AppException;
import vn.khanhduc.shoppingbackendservice.exception.ErrorCode;
import vn.khanhduc.shoppingbackendservice.mapper.UserMapper;
import vn.khanhduc.shoppingbackendservice.repository.UserRepository;
import vn.khanhduc.shoppingbackendservice.service.RedisService;
import vn.khanhduc.shoppingbackendservice.service.RoleService;
import vn.khanhduc.shoppingbackendservice.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreationResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = UserMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleService.findByName(UserType.USER);

        user.setRoles(Set.of(role));
        userRepository.save(user);

        String otp = GenerateOtp.generate();
        Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        data.put("verification_link", "http://localhost:8080/api/v1/users/confirm-email?email=" + user.getEmail() + "&secretCode=" + otp);
        EmailEvent event = EmailEvent.builder()
                .channel("Send Email")
                .subject("Verification Email")
                .recipient(user.getEmail())
                .templateCode("verification")
                .param(data)
                .build();

        rabbitTemplate.convertAndSend("emailQueue", event);

        redisService.saveOtp(user.getEmail(), otp);

        return UserMapper.toUserCreatedResponse(user);
    }

    @Override
    public VerifyOtpResponse verifyUser(String email, int otp) {
        String otpRedis = redisService.getOtp(email);
        log.info("Verification User {}", otpRedis);
        if(! Objects.equals(otp, Integer.parseInt(otpRedis))) {
            throw new AppException(ErrorCode.VERIFICATION_CODE_INVALID);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setUserType(UserStatus.ACTIVE);
        userRepository.save(user);

        redisService.deleteOtp(email);

        return VerifyOtpResponse.builder()
                .message("Account Verification Successful")
                .success(true)
                .build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserProfileResponse getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return UserProfileResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDateOfBirth())
                .gender(user.getGender())
                .build();
    }

}
