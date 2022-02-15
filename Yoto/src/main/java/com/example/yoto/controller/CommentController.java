package com.example.yoto.controller;

import com.example.yoto.model.comment.CommentService;
import com.example.yoto.model.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

//    @PostMapping("/comments")
//    public Comment create(@RequestBody Comment comment, HttpSession session, HttpServletRequest request){
//        userService.validateLogin(session, request);
//        Comment comment1 = commentService.createComment(comment);
//    }
//
//    @PutMapping("/users/{uid:[\\d]+}/edit_coment/{cid:[\\d]+}")
//    public Comment editComent(@RequestBody)
}
