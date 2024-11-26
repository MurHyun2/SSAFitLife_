package com.ssafy.ssafitlife.activity.model.dao;

import com.ssafy.ssafitlife.activity.model.dto.Activity;
import com.ssafy.ssafitlife.activity.model.dto.SaveActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityDao {
    List<Activity> selectAllActivities();
    List<SaveActivity> selectActivitiesByDate(SaveActivity activity);
    void insertSaveActivity(SaveActivity activity);  // insertActivity에서 변경
    void deleteActivitiesByDate(@Param("memNo") Integer memNo, @Param("date") String date);
    void insertActivity(Activity activity);  // 이 부분은 유지
    void updateActivity(Activity activity);
    void deleteActivity(Long actNo);
    List<Activity> selectRegisteredActivities(Integer memNo);
}