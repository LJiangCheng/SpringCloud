package com.springboot.auth.authorization.service.impl;

import com.springboot.auth.authorization.entity.Role;
import com.springboot.auth.authorization.provider.OrganizationProvider;
import com.springboot.auth.authorization.service.IRoleService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleService implements IRoleService {

    private final OrganizationProvider organizationProvider;

    public RoleService(OrganizationProvider organizationProvider) {
        this.organizationProvider = organizationProvider;
    }

    @Override
    public Set<Role> queryUserRolesByUserId(String userId) {
        return organizationProvider.queryRolesByUserId(userId).getData();
    }

}
