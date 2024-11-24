package com.ssafy.ssafitlife.security.controller;

import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import com.ssafy.ssafitlife.user.model.dto.User;
import com.ssafy.ssafitlife.user.model.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class AdminController {

    private final UserService userService;
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public User adminP(@AuthenticationPrincipal CustomUserDetails user) {
        user.getAuthorities().forEach(authority ->
                System.out.println("Authority = " + authority.getAuthority())
        );
        User userDetail = userService.findByEmail(user.getUsername());
        return userDetail;
    }
}
