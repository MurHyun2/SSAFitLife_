package com.ssafy.ssafitlife.food.model.service;

import com.ssafy.ssafitlife.food.model.dto.Diet;

import java.util.Date;
import java.util.List;

public interface DietService {
    List<Diet> getDietByDate(Diet diet);
    Boolean existsDietByNo(Long dietNo);
    void addDiet(Diet diet);
    void updateDiet(Diet diet);
    void deleteDiet(Diet diet);
}
