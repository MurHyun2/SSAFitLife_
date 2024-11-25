package com.ssafy.ssafitlife.calendar.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration("calendarConfig")
@MapperScan(basePackages = "com.ssafy.ssafitlife.calendar.model.dao")
public class DBConfig {
}
