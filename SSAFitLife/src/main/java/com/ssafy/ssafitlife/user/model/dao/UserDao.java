package com.ssafy.ssafitlife.user.model.dao;

import com.ssafy.ssafitlife.user.model.dto.User;

import java.util.List;

public interface UserDao {
    List<User> selectAll();
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
    User selectByEmail(String email);
    void insertUser(User user);
    void updateUser(User user);
    void deleteUser(Integer memNo);
    void updatePassword(User user);
}
