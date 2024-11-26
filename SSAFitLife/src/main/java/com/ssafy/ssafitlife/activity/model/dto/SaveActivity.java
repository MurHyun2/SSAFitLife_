package com.ssafy.ssafitlife.activity.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveActivity {
    private Long saveActNo;
    private Float actTime;
    private Integer memNo;
    private Long actNo;
    private Long actTempNo;
    private String actDate;
    private Integer totalMetabolism; // 총대사량

    private Activity activity;
}