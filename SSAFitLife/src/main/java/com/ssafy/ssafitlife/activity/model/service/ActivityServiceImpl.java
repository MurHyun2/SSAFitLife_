package com.ssafy.ssafitlife.activity.model.service;

import com.ssafy.ssafitlife.activity.model.dao.ActivityDao;
import com.ssafy.ssafitlife.activity.model.dto.Activity;
import com.ssafy.ssafitlife.activity.model.dto.SaveActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
    private final ActivityDao activityDao;

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
    public void updateSaveActivity(SaveActivity activity) {
        activityDao.updateSaveActivity(activity);
    }

    @Override
    @Transactional
    public void deleteSaveActivity(Long saveActNo) {
        activityDao.deleteSaveActivity(saveActNo);
    }
}