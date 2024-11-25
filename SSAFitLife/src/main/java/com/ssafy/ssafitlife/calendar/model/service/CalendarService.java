package com.ssafy.ssafitlife.calendar.model.service;

import com.ssafy.ssafitlife.calendar.model.dto.Calendar;

import java.util.List;

public interface CalendarService {

    List<Calendar> getAllCalendars(Integer memNo);
    Calendar getCalendar(Calendar calendar);
    void addCalendar(Calendar calendar);
    void modifyCalendar(Calendar calendar);
    void deleteCalendar(Calendar calendar);
}
