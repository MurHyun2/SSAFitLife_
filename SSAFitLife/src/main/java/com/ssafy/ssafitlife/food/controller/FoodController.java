package com.ssafy.ssafitlife.food.controller;

import com.ssafy.ssafitlife.food.model.dto.Food;
import com.ssafy.ssafitlife.food.model.service.FoodService;
import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<List<Food>> getAllFoods() {
        try {
            List<Food> foods = foodService.getAllFoods();
            return new ResponseEntity<>(foods, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR); // 빈 리스트와 500 오류 반환
        }
    }

    @GetMapping("/{foodNo}")
    public ResponseEntity<Food> getFoodByNo(@PathVariable Long foodNo) {
        try {
            Food food = foodService.getFoodByNo(foodNo);
            return food != null
                    ? new ResponseEntity<>(food, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND); // 음식이 없으면 404 반환
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 실패 시 500 오류 반환
        }
    }

    @PostMapping
    public ResponseEntity<String> addFood(@RequestBody Food food, @AuthenticationPrincipal CustomUserDetails user) {
        try {
            food.setMemNo(user.getMemNo());
            foodService.addFood(food);
            return new ResponseEntity<>("음식이 추가되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("음식을 추가하는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{foodNo}")
    public ResponseEntity<String> updateFood(@RequestBody Food food, @PathVariable Long foodNo) {
        try {
            food.setFoodNo(foodNo);
            foodService.updateFood(food);
            return new ResponseEntity<>("음식 정보가 업데이트되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("음식 정보를 업데이트하는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{foodNo}")
    public ResponseEntity<String> deleteFood(@PathVariable Long foodNo) {
        try {
            foodService.removeFood(foodNo);
            return new ResponseEntity<>("음식이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("음식을 삭제하는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/registered")
    public ResponseEntity<List<Food>> getRegisteredFoods(@AuthenticationPrincipal CustomUserDetails user) {
        try {
            List<Food> registeredFoods = foodService.getRegisteredFoods(user.getMemNo());
            return new ResponseEntity<>(registeredFoods, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR); // 빈 리스트와 500 오류 반환
        }
    }
}
