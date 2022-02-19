package com.example.yoto.controller;

import com.example.yoto.model.category.Category;
import com.example.yoto.model.category.CategoryComplexResponseDTO;
import com.example.yoto.model.category.CategoryService;
import com.example.yoto.model.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

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
    public int followCategory(@RequestParam int cid, HttpSession session, HttpServletRequest request){
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        return categoryService.followCategory(cid,uid);
    }

    @DeleteMapping("/categories/unfollow")
    public int unfollowCategory(@RequestParam int cid, HttpSession session,HttpServletRequest request){
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        return categoryService.unfollowCategory(cid,uid);
    }

    @PostMapping("/categories/add_to_video")
    public int addVideoInCategory(@RequestParam int vid,@RequestParam int cid, HttpSession session,HttpServletRequest request){
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        return categoryService.addVideoInCategory(vid,cid,uid);
    }

    @DeleteMapping("/categories/remove_video")
    public int removeVideoFromCategory(@RequestParam int vid,@RequestParam int cid, HttpSession session,HttpServletRequest request){
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        return categoryService.removeVideoFromCategory(vid,cid,uid);
    }

}
