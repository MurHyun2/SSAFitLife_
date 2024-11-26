package com.ssafy.ssafitlife.activity.model.service;

import com.ssafy.ssafitlife.activity.model.dto.Activity;
import com.ssafy.ssafitlife.activity.model.dto.SaveActivity;

import java.util.List;

public interface ActivityService {
    List<Activity> getAllActivities();
    List<SaveActivity> getActivitiesByDate(String date, Integer memNo);
    void updateSaveActivity(SaveActivity activity);
    void deleteSaveActivity(Long saveActNo);
}