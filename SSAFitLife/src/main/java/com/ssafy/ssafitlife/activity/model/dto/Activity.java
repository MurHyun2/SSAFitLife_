package com.ssafy.ssafitlife.activity.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
	private Long actNo;
	private Integer memNo;
	private String actName;
	private Float actInten;
	private Float actCalorie;
	private boolean isShared;
	private Integer actAddCount;
}