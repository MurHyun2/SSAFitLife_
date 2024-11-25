package com.ssafy.ssafitlife.user.model.service;

import com.ssafy.ssafitlife.user.model.dto.User;

import java.util.List;

public interface UserService {
    //전체 사용자 목록 불러오기
    public List<User> getUserList();
    //Email 중복 체크
    Boolean existsByEmail(String email);
    //닉네임 중복 체크
    Boolean existsByNickname(String nickname);
    //Email로 검색
    User findByEmail(String email);
    //등록
    void saveUser(User user);
    //수정
    void modifyUser(User user);
    //회원탈퇴
    void removeUser(Integer memNo);
    //비밀번호 업데이트
    void modifyPassword(User user);
}
