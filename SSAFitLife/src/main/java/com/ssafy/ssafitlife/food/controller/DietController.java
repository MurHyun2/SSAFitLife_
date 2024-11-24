package com.ssafy.ssafitlife.food.controller;

import com.ssafy.ssafitlife.food.model.dto.Diet;
import com.ssafy.ssafitlife.food.model.service.DietService;
import com.ssafy.ssafitlife.security.model.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diet")
public class DietController {
    private final DietService dietService;

    public DietController(DietService dietService) {
        this.dietService = dietService;
    }

    @GetMapping("/{dietDate}")
    public List<Diet> getDiet(@PathVariable String dietDate, @AuthenticationPrincipal CustomUserDetails user) {
        Diet diet = new Diet();
        diet.setMemNo(user.getMemNo());
        diet.setDietDate(dietDate);

        return dietService.getDietByDate(diet);
    }

    @PostMapping("/{dietDate}")
    public ResponseEntity<Void> addDiet(@PathVariable String dietDate, @RequestBody List<Diet> diets ,@AuthenticationPrincipal CustomUserDetails user) {
        Diet userDiet = new Diet();
        userDiet.setDietDate(dietDate);
        userDiet.setMemNo(user.getMemNo());
        dietService.deleteDiet(userDiet);

        for (Diet diet : diets) {
            diet.setMemNo(user.getMemNo());
            diet.setDietDate(dietDate);
            dietService.addDiet(diet);
        }

        return ResponseEntity.ok().build();
    }

}
