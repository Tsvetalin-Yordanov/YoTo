package com.example.yoto.controller;


import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.comment.CommentService;
import com.example.yoto.model.comment.CommentSimpleResponseDTO;
import com.example.yoto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private Util util;

    @PostMapping("/comments")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentSimpleResponseDTO create(@RequestBody Comment comment, HttpServletRequest request, @RequestParam int vid) {
        int uid = util.getUserIdFromRequest(request);
        CommentSimpleResponseDTO comment1 = commentService.createComment(comment, uid, vid);
        return comment1;
    }

    @PutMapping("/comments")
    public CommentSimpleResponseDTO editComment(@RequestBody Comment comment, HttpServletRequest request) {
        CommentSimpleResponseDTO comment1 = commentService.editComment(comment);
        return comment1;
    }

    @DeleteMapping("/comments")
    public ResponseEntity<String> deleteCommentById(@RequestParam int id, HttpServletRequest request) {
        commentService.deleteById(id);
        return ResponseEntity.status(204).body("Comment deleted successfully!");
    }

    @GetMapping("/comments/{id}")
    public CommentSimpleResponseDTO getById(@PathVariable int id) {
        CommentSimpleResponseDTO comment = commentService.getById(id);
        return comment;
    }

    @PostMapping("/comments/like")
    public CommentSimpleResponseDTO likeComment(@RequestParam int cid, HttpServletRequest request) {
        int uid = util.getUserIdFromRequest(request);
        CommentSimpleResponseDTO comment = commentService.likeComment(cid, uid);
        return comment;
    }

    @PostMapping("/comments/dislike")
    public CommentSimpleResponseDTO dislikeComment(@RequestParam int cid, HttpServletRequest request) {
        int uid = util.getUserIdFromRequest(request);
        CommentSimpleResponseDTO comment = commentService.dislikeComment(cid, uid);
        return comment;
    }

    @DeleteMapping("/comments/remove_reaction")
    public CommentSimpleResponseDTO removeReaction(@RequestParam int cid, HttpServletRequest request) {
        int uid = util.getUserIdFromRequest(request);
        CommentSimpleResponseDTO comment = commentService.removeReaction(cid, uid);
        return comment;
    }

    @PostMapping("/comments/respond")
    public CommentSimpleResponseDTO respondToComment(@RequestBody Comment comment, HttpServletRequest request, @RequestParam int cid) {
        int uid = util.getUserIdFromRequest(request);
        CommentSimpleResponseDTO comment1 = commentService.respondToComment(comment, uid, cid);
        return comment1;
    }

    @GetMapping("/comments/sub_comments")
    public List<CommentSimpleResponseDTO> showAllSubComments(@RequestParam int cid) {
        return commentService.getAllSubComments(cid);
    }

    @GetMapping("/comments/video")
    public List<CommentSimpleResponseDTO> showAllCommentsOfVideo(@RequestParam int vid){
        return commentService.getAllCommentsOfVideo(vid);
    }
}
