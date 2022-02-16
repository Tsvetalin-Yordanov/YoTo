package com.example.yoto.controller;

import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.comment.CommentService;
import com.example.yoto.model.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PostMapping("/comments")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Comment create(@RequestBody Comment comment, HttpSession session, HttpServletRequest request, @RequestParam int vid) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        Comment comment1 = commentService.createComment(comment, uid, vid);
        return comment1;
    }

    @PutMapping("/comments")
    public Comment editComment(@RequestBody Comment comment, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        Comment comment1 = commentService.editComment(comment);
        return comment1;
    }

    @DeleteMapping("/comments")
    public Comment deleteCommentById(@RequestParam int id, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        Comment comment = commentService.deleteById(id);
        return comment;
    }

    @GetMapping("/comments/{id}")
    public Comment getById(@PathVariable int id) {
        Comment comment = commentService.getById(id);
        return comment;
    }

    @PostMapping("/comments/like")
    public Comment likeComment(@RequestParam int cid, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        Comment comment = commentService.likeComment(cid, uid);
        return comment;
    }

    @PostMapping("/comments/dislike")
    public Comment dislikeComment(@RequestParam int cid, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        Comment comment = commentService.dislikeComment(cid, uid);
        return comment;
    }

    @DeleteMapping("/comments/remove_reaction")
    public Comment removeReaction(@RequestParam int cid, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        Comment comment = commentService.removeReaction(cid, uid);
        return comment;
    }

    @PostMapping("/comments/respond")
    public Comment respondToComment(@RequestBody Comment comment, HttpSession session, HttpServletRequest request, @RequestParam int cid) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        Comment comment1 = commentService.respondToComment(comment, uid, cid);
        return comment1;
    }
}
