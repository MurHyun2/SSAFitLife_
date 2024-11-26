package com.ssafy.ssafitlife.activity.model.service;

import com.ssafy.ssafitlife.activity.model.dao.ActivityDao;
import com.ssafy.ssafitlife.activity.model.dto.Activity;
import com.ssafy.ssafitlife.activity.model.dto.SaveActivity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityDao activityDao;

    ActivityServiceImpl(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityDao.selectAllActivities();
    }

    @Override
    public List<SaveActivity> getActivitiesByDate(String date, Integer memNo) {
        SaveActivity param = new SaveActivity();
        param.setActDate(date);
        param.setMemNo(memNo);
        return activityDao.selectActivitiesByDate(param);
    }

    @Override
    @Transactional
    public void saveActivities(List<SaveActivity> activities) {
        if (!activities.isEmpty()) {
            activityDao.deleteActivitiesByDate(activities.get(0).getMemNo(),
                    activities.get(0).getActDate());
            activities.forEach(activityDao::insertSaveActivity);
        }
    }

    @Override
    public void registerActivity(Activity activity) {
        activityDao.insertActivity(activity);
    }

    @Override
    public void updateActivity(Activity activity) {
        activityDao.updateActivity(activity);
    }

    @Override
    public void deleteActivity(Long actNo) {
        activityDao.deleteActivity(actNo);
    }

    @Override
    public List<Activity> getRegisteredActivities(Integer memNo) {
        return activityDao.selectRegisteredActivities(memNo);
    }
}