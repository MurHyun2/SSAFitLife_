package com.ssafy.ssafitlife.food.controller;

import com.ssafy.ssafitlife.food.model.dto.Food;
import com.ssafy.ssafitlife.food.model.service.FoodService;
import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import com.ssafy.ssafitlife.user.model.dto.User;
import com.ssafy.ssafitlife.user.model.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/food")
public class FoodController {

    private final FoodService foodService;
    private final UserService userService;

    public FoodController(FoodService foodService, UserService userService) {
        this.foodService = foodService;
        this.userService = userService;
    }

    @GetMapping
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    @GetMapping("/{foodNo}")
    public Food getFoodByNo(@PathVariable Long foodNo) {
        return foodService.getFoodByNo(foodNo);
    }

    @PostMapping
    public void addFood(@RequestBody Food food, @AuthenticationPrincipal CustomUserDetails user) {
        food.setMemNo(user.getMemNo());
        foodService.addFood(food);
    }

    @PutMapping("/{foodNo}")
    public void updateFood(@RequestBody Food food, @PathVariable Long foodNo) {
        food.setFoodNo(foodNo);
        foodService.updateFood(food);
    }

    @DeleteMapping("/{foodNo}")
    public void deleteFood(@PathVariable Long foodNo) {
        foodService.removeFood(foodNo);
    }
}

