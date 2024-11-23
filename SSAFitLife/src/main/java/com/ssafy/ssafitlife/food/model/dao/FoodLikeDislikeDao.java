package com.ssafy.ssafitlife.food.model.dao;

import com.ssafy.ssafitlife.food.model.dto.FoodLikeDislike;

import java.util.List;

public interface FoodLikeDislikeDao {
    long selectByFoodNo(Long foodNo);
    boolean existsByFoodNoMemNo(FoodLikeDislike likeDislike);
    void insertLikeDislike(FoodLikeDislike likeDislike);
    void updateLikeDislike(FoodLikeDislike likeDislike);
    void deleteLikeDislike(FoodLikeDislike likeDislike);
}
