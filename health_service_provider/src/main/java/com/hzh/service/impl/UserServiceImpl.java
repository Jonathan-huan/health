package com.hzh.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hzh.dao.PermissionDao;
import com.hzh.dao.RoleDao;
import com.hzh.dao.UserDao;
import com.hzh.pojo.Permission;
import com.hzh.pojo.Role;
import com.hzh.pojo.User;
import com.hzh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {
   @Autowired
   private UserDao userDao;
   @Autowired
   private RoleDao roleDao;
   @Autowired
   private PermissionDao permissionDao;

    @Override
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);
        if(user==null){
            return null;
        }
        Set<Role> roles = roleDao.findByUserId(user.getId());
        for (Role role : roles) {
            Integer id = role.getId();
            Set<Permission> permissions = permissionDao.findByRoleId(id);
            role.setPermissions(permissions);
        }
        user.setRoles(roles);
        return user;
    }
}
