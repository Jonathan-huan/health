package com.hzh.dao;

import com.hzh.pojo.Role;

import java.util.Set;

public interface RoleDao {
    public Set<Role> findByUserId(Integer id);
}
