package vn.khanhduc.shoppingbackendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.khanhduc.shoppingbackendservice.entity.Role;
import vn.khanhduc.shoppingbackendservice.exception.AppException;
import vn.khanhduc.shoppingbackendservice.exception.ErrorCode;
import vn.khanhduc.shoppingbackendservice.repository.RoleRepository;
import vn.khanhduc.shoppingbackendservice.service.RoleService;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        log.info("Find Role By Name");
        return roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
    }

    @Override
    public void save(Role role) {
        log.info("Create Name");
        Optional<Role> existingRole = roleRepository.findByName(role.getName());
        if(existingRole.isPresent()) {
            log.error("Role already exists");
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        log.info("Save Role Success");
        roleRepository.save(role);
    }

}
