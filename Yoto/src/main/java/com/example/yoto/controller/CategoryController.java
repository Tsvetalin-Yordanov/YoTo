package com.example.yoto.controller;

import com.example.yoto.model.category.Category;
import com.example.yoto.model.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories/{id:[\\d]+}")
    public Category getById(@PathVariable int id) {
        Category category = categoryService.getById(id);
        return category;
    }

    @GetMapping("/categories")
    public List<Category> getAll() {
        List<Category> categories = categoryService.getAll();
        return categories;
    }
}
