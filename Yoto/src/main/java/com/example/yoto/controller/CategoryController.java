package com.example.yoto.controller;

import com.example.yoto.model.category.CategoryComplexResponseDTO;
import com.example.yoto.model.category.CategoryService;
import com.example.yoto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private Util util;

    @GetMapping("/categories/{id}")
    public CategoryComplexResponseDTO getById(@PathVariable int id) {
        CategoryComplexResponseDTO category = categoryService.getById(id);
        return category;
    }

    @GetMapping("/categories")
    public List<CategoryComplexResponseDTO> getAll() {
        List<CategoryComplexResponseDTO> categories = categoryService.getAll();
        return categories;
    }

    @PostMapping("/categories/follow")
    public int followCategory(@RequestParam int cid, HttpServletRequest request){
        int uid =  util.getUserIdFromRequest(request);
        return categoryService.followCategory(cid,uid);
    }

    @DeleteMapping("/categories/unfollow")
    public int unfollowCategory(@RequestParam int cid,HttpServletRequest request){
        int uid =  util.getUserIdFromRequest(request);
        return categoryService.unfollowCategory(cid,uid);
    }

    @PostMapping("/categories/add_to_video")
    public int addVideoInCategory(@RequestParam int vid,@RequestParam int cid,HttpServletRequest request){
        int uid =  util.getUserIdFromRequest(request);
        return categoryService.addVideoInCategory(vid,cid,uid);
    }

    @DeleteMapping("/categories/remove_video")
    public int removeVideoFromCategory(@RequestParam int vid,@RequestParam int cid,HttpServletRequest request){
        int uid =  util.getUserIdFromRequest(request);
        return categoryService.removeVideoFromCategory(vid,cid,uid);
    }

}
