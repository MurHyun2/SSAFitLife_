package com.ssafy.ssafitlife.food.controller;

import com.ssafy.ssafitlife.food.model.dto.Diet;
import com.ssafy.ssafitlife.food.model.service.DietService;
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
    public List<Diet> getDiet(@PathVariable String dietDate) {
        Diet diet = new Diet();
        diet.setMemNo(1);
        diet.setDietDate(dietDate);

        System.out.println("diet = " + diet);

        List<Diet> list = dietService.getDietByDate(diet);
        for (Diet dt : list) {
            System.out.println("diet = " + dt);
        }
        return dietService.getDietByDate(diet);
    }
}
