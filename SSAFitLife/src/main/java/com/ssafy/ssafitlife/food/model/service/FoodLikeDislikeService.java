package com.ssafy.ssafitlife.food.model.service;

import com.ssafy.ssafitlife.food.model.dto.FoodLikeDislike;

public interface FoodLikeDislikeService {
    long getLikeDislikeCountByFoodNo(Long foodNo);
    boolean existsByFoodNoMemNo(FoodLikeDislike likeDislike);
    void addLikeDislike(FoodLikeDislike likeDislike);
    void updateLikeDislike(FoodLikeDislike likeDislike);
    void deleteLikeDislike(FoodLikeDislike likeDislike);
}