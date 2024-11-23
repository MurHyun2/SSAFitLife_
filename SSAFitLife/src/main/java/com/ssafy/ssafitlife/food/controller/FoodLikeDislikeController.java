package com.ssafy.ssafitlife.food.controller;

import com.ssafy.ssafitlife.food.model.dto.FoodLikeDislike;
import com.ssafy.ssafitlife.food.model.service.FoodLikeDislikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food-like-dislike")
public class FoodLikeDislikeController {

    private final FoodLikeDislikeService foodLikeDislikeService;

    public FoodLikeDislikeController(FoodLikeDislikeService foodLikeDislikeService) {
        this.foodLikeDislikeService = foodLikeDislikeService;
    }

    @GetMapping("/{foodNo}")
    public long getLikeDislikeCount(@PathVariable Long foodNo) {
        return foodLikeDislikeService.getLikeDislikeCountByFoodNo(foodNo);
    }

    @PostMapping("/{foodNo}")
    public void addLikeDislike(@PathVariable Long foodNo, @RequestBody FoodLikeDislike likeDislike) {
        likeDislike.setFoodNo(foodNo);

        if(foodLikeDislikeService.existsByFoodNoMemNo(likeDislike)) {
            foodLikeDislikeService.deleteLikeDislike(likeDislike);

            foodLikeDislikeService.addLikeDislike(likeDislike);
            return;
        }

        foodLikeDislikeService.addLikeDislike(likeDislike);
    }
}