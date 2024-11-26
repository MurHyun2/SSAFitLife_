package com.ssafy.ssafitlife.calendar.controller;

import com.ssafy.ssafitlife.calendar.model.dto.Calendar;
import com.ssafy.ssafitlife.calendar.model.service.CalendarService;
import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    // 의존성 주입
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/weight-data")
    public ResponseEntity<Object> listByMemNo(@AuthenticationPrincipal CustomUserDetails user) {

        List<Calendar> calendars = calendarService.getAllCalendars(user.getMemNo());

        return ResponseEntity.status(HttpStatus.OK).body(calendars);
    }

    @GetMapping("/weight-data/{date}")
    public ResponseEntity<Object> getWeight(@PathVariable String date, @AuthenticationPrincipal CustomUserDetails user) {
        Calendar calendarInfo = new Calendar();
        calendarInfo.setDate(date);
        calendarInfo.setMemNo(user.getMemNo());

        Calendar calendarDetail = calendarService.getCalendar(calendarInfo);

        return ResponseEntity.status(HttpStatus.OK).body(calendarDetail);
    }

    @PostMapping("/weight-add")
    public ResponseEntity<Object> addWeight(@RequestBody Calendar calendar, @AuthenticationPrincipal CustomUserDetails user) {
        calendar.setMemNo(user.getMemNo());
        calendarService.addCalendar(calendar);

        return ResponseEntity.status(HttpStatus.OK).body("몸무게 정보 입력 완료");
    }
    
    @PutMapping("/weight-update")
    public ResponseEntity<Object> updateWeight(@RequestBody Calendar calendar, @AuthenticationPrincipal CustomUserDetails user) {

        calendar.setMemNo(user.getMemNo());
        calendarService.modifyCalendar(calendar);

        return ResponseEntity.status(HttpStatus.OK).body("몸무게 정보 수정 완료");
    }

    @DeleteMapping("/weight-delete/{date}")
    public ResponseEntity<Object> deleteWeight(@PathVariable String date, @AuthenticationPrincipal CustomUserDetails user) {
        Calendar calendar = new Calendar();
        calendar.setMemNo(user.getMemNo());
        calendar.setDate(date);
        calendarService.deleteCalendar(calendar);

        return ResponseEntity.status(HttpStatus.OK).body("몸무게 정보 삭제 완료");
    }

}