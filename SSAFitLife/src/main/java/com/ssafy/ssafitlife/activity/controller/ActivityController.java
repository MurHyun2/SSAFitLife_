package com.ssafy.ssafitlife.activity.controller;

import com.ssafy.ssafitlife.activity.model.dto.Activity;
import com.ssafy.ssafitlife.activity.model.dto.SaveActivity;
import com.ssafy.ssafitlife.activity.model.service.ActivityService;
import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    private final ActivityService activityService;

    ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    // 전체 활동 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    // 특정 날짜의 활동 조회
    @GetMapping("/{date}")
    public ResponseEntity<List<SaveActivity>> getActivities(@PathVariable String date,
                                                            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(activityService.getActivitiesByDate(date, user.getMemNo()));
    }

    // 활동 저장
    @PostMapping("/{date}")
    public ResponseEntity<Void> saveActivities(@PathVariable String date,
                                               @RequestBody List<SaveActivity> activities,
                                               @AuthenticationPrincipal CustomUserDetails user) {
        activities.forEach(activity -> {
            activity.setMemNo(user.getMemNo());
            activity.setActDate(date);
        });
        activityService.saveActivities(activities);
        return ResponseEntity.ok().build();
    }

    // 활동 등록
    @PostMapping
    public ResponseEntity<String> registerActivity(@RequestBody Activity activity,
                                                   @AuthenticationPrincipal CustomUserDetails user) {
        activity.setMemNo(user.getMemNo());
        activity.setActCalorie(activity.getActInten() * 50);
        activityService.registerActivity(activity);
        return ResponseEntity.ok("활동이 등록되었습니다.");
    }

    // 활동 수정
    @PutMapping("/{actNo}")
    public ResponseEntity<String> updateActivity(@PathVariable Long actNo,
                                                 @RequestBody Activity activity,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        activity.setActNo(actNo);
        activity.setMemNo(user.getMemNo());
        activity.setActCalorie(activity.getActInten() * 50);
        activityService.updateActivity(activity);
        return ResponseEntity.ok("활동이 수정되었습니다.");
    }

    // 활동 삭제
    @DeleteMapping("/{actNo}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long actNo) {
        activityService.deleteActivity(actNo);
        return ResponseEntity.ok("활동이 삭제되었습니다.");
    }

    @GetMapping("/registered")
    public ResponseEntity<List<Activity>> getRegisteredActivities(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(activityService.getRegisteredActivities(user.getMemNo()));
    }
}