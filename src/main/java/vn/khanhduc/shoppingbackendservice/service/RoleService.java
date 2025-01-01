package vn.khanhduc.shoppingbackendservice.service;

import vn.khanhduc.shoppingbackendservice.entity.Role;

public interface RoleService {
    Role findByName(String name);
    void save(Role role);
}
