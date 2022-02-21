package com.example.yoto.model.comment;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.relationship.CHC.CommentHasComment;
import com.example.yoto.model.relationship.CHC.CommentHasCommentID;
import com.example.yoto.model.relationship.CHC.CommentHasCommentRepository;
import com.example.yoto.model.relationship.URTC.UserReactToComment;
import com.example.yoto.model.relationship.URTC.UserReactToCommentID;
import com.example.yoto.model.relationship.URTC.UserReactToCommentRepository;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import com.example.yoto.model.user.UserService;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserReactToCommentRepository userReactToCommentRepository;
    @Autowired
    private CommentHasCommentRepository commentHasCommentRepository;

    public CommentSimpleResponseDTO createComment(Comment comment, int uid, int vid) {
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new BadRequestException("text is mandatory");
        }
        Video video = getVideoById(vid);
        comment.setCreator(getUserById(uid));
        comment.setVideo(video);
        comment.setCreationDate(LocalDateTime.now());
        commentRepository.save(comment);
        return commentToCommentDTO(comment);
    }

    public CommentSimpleResponseDTO editComment(Comment comment) {
        Comment comment1 = getCommentById(comment.getId());

        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new BadRequestException("text is mandatory");
        }
        comment1.setText(comment.getText());
        comment1.setCreationDate(LocalDateTime.now());
        commentRepository.save(comment1);
        return commentToCommentDTO(comment1);
    }

    public CommentSimpleResponseDTO deleteById(int id) {
        if (id > 0) {
            Comment comment = getCommentById(id);
            commentRepository.deleteById(id);
            return commentToCommentDTO(comment);
        }
        throw new BadRequestException("Id is not positive");
    }

    public CommentSimpleResponseDTO getById(int id) {
        if (id > 0) {
            Comment comment = getCommentById(id);
            return commentToCommentDTO(comment);
        }
        throw new BadRequestException("Id is not positive");
    }

    public CommentSimpleResponseDTO likeComment(int cid, int uid) {
        CommentSimpleResponseDTO commentDto = reactToComment(cid, uid, '+');
        return commentDto;
    }

    public CommentSimpleResponseDTO dislikeComment(int cid, int uid) {
        CommentSimpleResponseDTO commentDto = reactToComment(cid, uid, '-');
        return commentDto;
    }

    public CommentSimpleResponseDTO reactToComment(int cid, int uid, char reaction) {
        if (cid > 0) {
            Comment comment = getCommentById(cid);
            User user = getUserById(uid);

            UserReactToCommentID userReactToCommentID = new UserReactToCommentID();
            userReactToCommentID.setCommentId(cid);
            userReactToCommentID.setUserId(uid);
            UserReactToComment userReactToComment = new UserReactToComment();
            userReactToComment.setId(userReactToCommentID);
            userReactToComment.setComment(comment);
            userReactToComment.setUser(user);
            userReactToComment.setReaction(reaction);
            userReactToCommentRepository.save(userReactToComment);


            return commentToCommentDTO(comment);
        }
        throw new BadRequestException("Id is not positive");
    }

    public CommentSimpleResponseDTO removeReaction(int cid, int uid) {
        if (cid > 0) {
            Comment comment = getCommentById(cid);
            UserReactToCommentID userReactToCommentID = new UserReactToCommentID();
            userReactToCommentID.setCommentId(cid);
            userReactToCommentID.setUserId(uid);
            Optional<UserReactToComment> optional = userReactToCommentRepository.findById(userReactToCommentID);
            if (optional.isPresent()) {
                userReactToCommentRepository.deleteById(userReactToCommentID);
                return commentToCommentDTO(comment);
            }
            throw new BadRequestException("You haven't reacted to this comment yet");
        }
        throw new BadRequestException("Id is not positive");
    }

    public CommentSimpleResponseDTO respondToComment(Comment comment, int uid, int cid) {
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new BadRequestException("text is mandatory");
        }
        if (cid > 0) {
            Comment comment1 = getCommentById(cid);
            comment.setCreator(getUserById(uid));
            comment.setVideo(getVideoById(comment1.getVideo().getId()));
            comment.setCreationDate(LocalDateTime.now());
            commentRepository.save(comment);


            CommentHasCommentID commentHasCommentID = new CommentHasCommentID();
            commentHasCommentID.setParentId(cid);
            commentHasCommentID.setChildId(comment.getId());

            CommentHasComment commentHasComment = new CommentHasComment();
            commentHasComment.setId(commentHasCommentID);
            commentHasComment.setParent(comment1);
            commentHasComment.setChild(comment);

            commentHasCommentRepository.save(commentHasComment);
            return commentToCommentDTO(comment1);
        }
        throw new BadRequestException("Id is not positive");
    }


    private CommentSimpleResponseDTO commentToCommentDTO(Comment comment) {
        int likes = userReactToCommentRepository.findAllByReactionAndCommentId('+', comment.getId()).size();
        int dislikes = userReactToCommentRepository.findAllByReactionAndCommentId('-', comment.getId()).size();

        CommentSimpleResponseDTO dto = new CommentSimpleResponseDTO();
        dto.setId(comment.getId());
        dto.setCreator(UserService.userToSimpleDTO(comment.getCreator()));
        dto.setText(comment.getText());
        dto.setCreationDate(comment.getCreationDate());
        dto.setLikes(likes);
        dto.setDislikes(dislikes);
        dto.setSubComments(comment.getSubComments().size());
        return dto;
    }

    public List<CommentSimpleResponseDTO> getAllSubComments(int cid) {
        List<CommentSimpleResponseDTO> dtos = new ArrayList<>();
        List<CommentHasComment> subComments = commentHasCommentRepository.findAllByParent(getCommentById(cid));
        for (CommentHasComment chc : subComments) {
            dtos.add(commentToCommentDTO(chc.getChild()));
        }
        return dtos;
    }

    //TODO move in Util
    private Comment getCommentById(int id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    //TODO move in Util
    private User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    //TODO move in Util
    private Video getVideoById(int id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }
}
