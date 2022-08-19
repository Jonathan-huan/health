package com.hzh.dao;

import com.hzh.pojo.Permission;

import java.util.Set;

public interface PermissionDao {
    public Set<Permission> findByRoleId(Integer id);
}
