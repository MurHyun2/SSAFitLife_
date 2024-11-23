package com.ssafy.ssafitlife.food.model.service;

import com.ssafy.ssafitlife.food.model.dao.FoodLikeDislikeDao;
import com.ssafy.ssafitlife.food.model.dto.FoodLikeDislike;
import org.springframework.stereotype.Service;

@Service
public class FoodLikeDislikeServiceImpl implements FoodLikeDislikeService {

    private final FoodLikeDislikeDao foodLikeDislikeDao;

    FoodLikeDislikeServiceImpl(FoodLikeDislikeDao foodLikeDislikeDao) {
        this.foodLikeDislikeDao = foodLikeDislikeDao;
    }

    @Override
    public long getLikeDislikeCountByFoodNo(Long foodNo) {
        return foodLikeDislikeDao.selectByFoodNo(foodNo);
    }

    @Override
    public boolean existsByFoodNoMemNo(FoodLikeDislike foodLikeDislike) {
        return foodLikeDislikeDao.existsByFoodNoMemNo(foodLikeDislike);
    }

    @Override
    public void addLikeDislike(FoodLikeDislike foodLikeDislike) {
        foodLikeDislikeDao.insertLikeDislike(foodLikeDislike);
    }

    @Override
    public void updateLikeDislike(FoodLikeDislike foodLikeDislike) {
        foodLikeDislikeDao.updateLikeDislike(foodLikeDislike);
    }

    @Override
    public void deleteLikeDislike(FoodLikeDislike foodLikeDislike) {
        foodLikeDislikeDao.deleteLikeDislike(foodLikeDislike);
    }
}
