package com.ssafy.ssafitlife.calendar.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Calendar {
	private Integer calendarNo; //캘린더 번호 (고유값)
	private Integer memNo; // 멤버 번호
	private String date; //날짜
	private Float weight; //몸무게
	private Float skeletalMuscleMass; //골격근량
	private Float bodyFatPercentage; //체지방률
	private Integer dietNo; //식단 번호
	private Integer SaveActNo ; //활동 저장 번호

}