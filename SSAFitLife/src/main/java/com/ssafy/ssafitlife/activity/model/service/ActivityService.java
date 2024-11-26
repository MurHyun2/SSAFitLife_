package com.ssafy.ssafitlife.activity.model.service;

import com.ssafy.ssafitlife.activity.model.dto.Activity;
import com.ssafy.ssafitlife.activity.model.dto.SaveActivity;

import java.util.List;

public interface ActivityService {
    List<Activity> getAllActivities();
    List<SaveActivity> getActivitiesByDate(String date, Integer memNo);
    void saveActivities(List<SaveActivity> activities);
    void registerActivity(Activity activity);
    void updateActivity(Activity activity);
    void deleteActivity(Long actNo);
    List<Activity> getRegisteredActivities(Integer memNo);
}