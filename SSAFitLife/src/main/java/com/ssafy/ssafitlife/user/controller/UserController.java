package com.ssafy.ssafitlife.user.controller;

import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import com.ssafy.ssafitlife.user.model.dto.User;
import com.ssafy.ssafitlife.user.model.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/userInfo")
    public ResponseEntity<User> getUserInfo(@AuthenticationPrincipal CustomUserDetails user) {
        User userDetail = userService.findByEmail(user.getUsername());
        userDetail.setPassword(null);
        return new ResponseEntity<>(userDetail, HttpStatus.OK);
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam(value = "email") String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam(value = "nickname") String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return ResponseEntity.ok(exists);
    }

    // 회원가입 처리
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody User user) {
        try {
            userService.saveUser(user);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패");
        }
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody User user) {
        try {
            userService.modifyUser(user);
            return ResponseEntity.ok("회원수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원수정 실패");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@AuthenticationPrincipal CustomUserDetails user) {
        try {
            userService.removeUser(user.getMemNo());
            return ResponseEntity.ok("회원탈퇴 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원탈퇴 실패");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> passwordData, @AuthenticationPrincipal CustomUserDetails user) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");

            User currentUser = userService.findByEmail(user.getUsername());

            if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("기존 비밀번호가 일치하지 않습니다.");
            }

            String encodedNewPassword = passwordEncoder.encode(newPassword);

            currentUser.setPassword(encodedNewPassword);
            userService.modifyPassword(currentUser);

            return ResponseEntity.ok("비밀번호 수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 수정 실패");
        }
    }
}
