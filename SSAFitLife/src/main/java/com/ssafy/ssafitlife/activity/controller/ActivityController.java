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

    // 전체 운동 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    // 특정 날짜의 저장된 활동 조회
    @GetMapping("/{date}")
    public ResponseEntity<List<SaveActivity>> getActivities(@PathVariable String date, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(activityService.getActivitiesByDate(date, user.getMemNo()));
    }

    // 저장된 활동 수정
    @PutMapping("/save/{saveActNo}")
    public ResponseEntity<String> updateSaveActivity(@PathVariable Long saveActNo, @RequestBody SaveActivity activity, @AuthenticationPrincipal CustomUserDetails user) {
        activity.setMemNo(user.getMemNo());
        activity.setSaveActNo(saveActNo);
        activityService.updateSaveActivity(activity);
        return ResponseEntity.ok("활동이 수정되었습니다.");
    }

    // 저장된 활동 삭제
    @DeleteMapping("/save/{saveActNo}")
    public ResponseEntity<String> deleteSaveActivity(@PathVariable Long saveActNo) {
        activityService.deleteSaveActivity(saveActNo);
        return ResponseEntity.ok("활동이 삭제되었습니다.");
    }
}