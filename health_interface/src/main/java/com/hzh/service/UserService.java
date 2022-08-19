package com.hzh.service;

import com.hzh.pojo.User;

public interface UserService {
    public User findByUsername(String username);
}
