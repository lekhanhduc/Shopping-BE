package vn.khanhduc.shoppingbackendservice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.khanhduc.shoppingbackendservice.common.UserStatus;
import vn.khanhduc.shoppingbackendservice.common.UserType;
import vn.khanhduc.shoppingbackendservice.entity.Role;
import vn.khanhduc.shoppingbackendservice.entity.User;
import vn.khanhduc.shoppingbackendservice.repository.RoleRepository;
import vn.khanhduc.shoppingbackendservice.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfiguration {

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                                  RoleRepository roleRepository
                                                  ) {
        log.debug("Initializing application.....");
        return args -> {
            Optional<Role> userRole = roleRepository.findByName(UserType.USER);
            if(userRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(UserType.USER)
                        .description("Role User")
                        .build());
            }

            Optional<Role> adminRole = roleRepository.findByName(UserType.ADMIN);
            if(adminRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(UserType.ADMIN)
                        .description("Role Admin")
                        .build());
            }
            Optional<Role> managerRole = roleRepository.findByName(UserType.MANAGER);
            if(managerRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(UserType.MANAGER)
                        .description("Role Manager")
                        .build());
            }

            Optional<Role> sellerRole = roleRepository.findByName(UserType.SELLER);
            if(sellerRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(UserType.SELLER)
                        .description("Role Seller")
                        .build());
            }

            Optional<Role> supportRole = roleRepository.findByName(UserType.SUPPORT);
            if(supportRole.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(UserType.SUPPORT)
                        .description("Role Support")
                        .build());
            }

            Optional<User> user = userRepository.findByEmail(email);
            List<Role> roles = roleRepository.findAll();
            Set<Role> roleSet = new HashSet<>(roles);

            if(user.isEmpty()) {
                userRepository.save(User.builder()
                                .name("Admin")
                                .email(email)
                                .password(passwordEncoder.encode(password))
                                .userType(UserStatus.ACTIVE)
                                .roles(roleSet)
                        .build());
                log.warn("Admin user has been created with default password: 123456, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
