package com.ssafy.ssafitlife.food.model.service;

import com.ssafy.ssafitlife.food.model.dao.DietDao;
import com.ssafy.ssafitlife.food.model.dto.Diet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DietServiceImpl implements DietService {

    private final DietDao dietDao;

    public DietServiceImpl(DietDao dietDao) {
        this.dietDao = dietDao;
    }

    @Override
    public List<Diet> getDietByDate(Diet diet) {
        return dietDao.selectDietByDate(diet);
    }

    @Override
    public Boolean existsDietByNo(Long dietNo) {
        return dietDao.existsDietByNo(dietNo);
    }

    @Override
    public void addDiet(Diet diet) {
        dietDao.insertDiet(diet);
    }

    @Override
    public void updateDiet(Diet diet) {
        dietDao.updateDiet(diet);
    }

    @Override
    public void deleteDiet(Diet diet) {
        dietDao.deleteDiet(diet);
    }

}
