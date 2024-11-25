package com.ssafy.ssafitlife.calendar.model.dao;

import com.ssafy.ssafitlife.calendar.model.dto.Calendar;

import java.util.List;

public interface CalendarDao {
    List<Calendar> selectAllCalendars(Integer memNo);
    Calendar selectCalendar(Calendar calendar);
    void insertCalendar(Calendar calendar);
    void updateCalendar(Calendar calendar);
    void deleteCalendar(Calendar calendar);
}