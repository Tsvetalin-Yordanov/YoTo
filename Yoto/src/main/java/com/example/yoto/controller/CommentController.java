package com.example.yoto.controller;

import com.example.yoto.model.category.CategoryComplexResponseDTO;
import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.comment.CommentService;
import com.example.yoto.model.comment.CommentSimpleResponseDTO;
import com.example.yoto.model.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PostMapping("/comments")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentSimpleResponseDTO create(@RequestBody Comment comment, HttpSession session, HttpServletRequest request, @RequestParam int vid) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        CommentSimpleResponseDTO comment1 = commentService.createComment(comment, uid, vid);
        return comment1;
    }

    @PutMapping("/comments")
    public CommentSimpleResponseDTO editComment(@RequestBody Comment comment, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        CommentSimpleResponseDTO comment1 = commentService.editComment(comment);
        return comment1;
    }

    @DeleteMapping("/comments")
    public CommentSimpleResponseDTO deleteCommentById(@RequestParam int id, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        CommentSimpleResponseDTO comment = commentService.deleteById(id);
        return comment;
    }

    @GetMapping("/comments/{id}")
    public CommentSimpleResponseDTO getById(@PathVariable int id) {
        CommentSimpleResponseDTO comment = commentService.getById(id);
        return comment;
    }

    @PostMapping("/comments/like")
    public CommentSimpleResponseDTO likeComment(@RequestParam int cid, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        CommentSimpleResponseDTO comment = commentService.likeComment(cid, uid);
        return comment;
    }

    @PostMapping("/comments/dislike")
    public CommentSimpleResponseDTO dislikeComment(@RequestParam int cid, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        CommentSimpleResponseDTO comment = commentService.dislikeComment(cid, uid);
        return comment;
    }

    @DeleteMapping("/comments/remove_reaction")
    public CommentSimpleResponseDTO removeReaction(@RequestParam int cid, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        CommentSimpleResponseDTO comment = commentService.removeReaction(cid, uid);
        return comment;
    }

    @PostMapping("/comments/respond")
    public CommentSimpleResponseDTO respondToComment(@RequestBody Comment comment, HttpSession session, HttpServletRequest request, @RequestParam int cid) {
        userService.validateLogin(session, request);
        int uid = (int) session.getAttribute("user_id");
        CommentSimpleResponseDTO comment1 = commentService.respondToComment(comment, uid, cid);
        return comment1;
    }

    @GetMapping("/comments/sub_comments")
    public List<CommentSimpleResponseDTO> showAllSubComments(@RequestParam int cid,HttpSession session, HttpServletRequest request){
        return commentService.getAllSubComments(cid);
    }
}
