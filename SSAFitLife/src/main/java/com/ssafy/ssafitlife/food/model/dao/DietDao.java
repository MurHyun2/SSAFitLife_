package com.ssafy.ssafitlife.food.model.dao;

import com.ssafy.ssafitlife.food.model.dto.Diet;

import java.util.Date;
import java.util.List;

public interface DietDao {
    List<Diet> selectDietByDate(Diet diet);
    Boolean existsDietByNo(Long dietNo);
    void insertDiet(Diet diet);
    void updateDiet(Diet diet);
    void deleteDiet(Diet diet);
}
