package com.ssafy.ssafitlife.calendar.model.service;

import com.ssafy.ssafitlife.calendar.model.dao.CalendarDao;
import com.ssafy.ssafitlife.calendar.model.dto.Calendar;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService {
    private final CalendarDao calendarDao;

    public CalendarServiceImpl(CalendarDao calendarDao) {
        this.calendarDao = calendarDao;
    }

    @Override
    public List<Calendar> getAllCalendars(Integer memNo) {
        return calendarDao.selectAllCalendars(memNo);
    }

    @Override
    public Calendar getCalendar(Calendar calendar) {
        return calendarDao.selectCalendar(calendar);
    }

    @Override
    public void addCalendar(Calendar calendar) {
        calendarDao.insertCalendar(calendar);
    }

    @Override
    public void modifyCalendar(Calendar calendar) {
        calendarDao.updateCalendar(calendar);
    }

    @Override
    public void deleteCalendar(Calendar calendar) {
        calendarDao.deleteCalendar(calendar);
    }
}