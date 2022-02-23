package com.example.yoto.model.comment;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.relationship.commentsHaveComments.CommentHasComment;
import com.example.yoto.model.relationship.commentsHaveComments.CommentHasCommentID;
import com.example.yoto.model.relationship.userReactToComments.UserReactToComment;
import com.example.yoto.model.relationship.userReactToComments.UserReactToCommentID;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserService;
import com.example.yoto.model.video.Video;
import com.example.yoto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;

import static com.example.yoto.util.Util.COMMENT_TXT_MAX_LENGTH;

@Service
public class CommentService {

    @Autowired
    private Util util;

    public CommentSimpleResponseDTO createComment(Comment comment, int uid, int vid) {
        String text = comment.getText();
        if (text.trim().isEmpty() || text.length() > COMMENT_TXT_MAX_LENGTH) {
            throw new BadRequestException("Invalid comment text!");
        }
        Video video = util.videoGetById(vid);
        comment.setCreator(util.userGetById(uid));
        comment.setVideo(video);
        comment.setCreationDate(LocalDateTime.now());
        util.commentRepository.save(comment);
        return commentToCommentDTO(comment);
    }

    public CommentSimpleResponseDTO editComment(Comment comment) {
        Comment comment1 = util.commentGetById(comment.getId());
        String text = comment.getText();
        if (text.trim().isEmpty() || text.length() > COMMENT_TXT_MAX_LENGTH) {
            throw new BadRequestException("Invalid comment text!");
        }
        comment1.setText(text);
        comment1.setCreationDate(LocalDateTime.now());
        util.commentRepository.save(comment1);
        return commentToCommentDTO(comment1);
    }

    public void deleteById(int id) {
        if (id > 0) {
            util.commentGetById(id);
            util.commentRepository.deleteById(id);
        } else {
            throw new BadRequestException("Id is not positive");
        }
    }

    public CommentSimpleResponseDTO getById(int id) {
        if (id > 0) {
            Comment comment = util.commentGetById(id);
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
            Comment comment = util.commentGetById(cid);
            User user = util.userGetById(uid);

            UserReactToCommentID userReactToCommentID = new UserReactToCommentID();
            userReactToCommentID.setCommentId(cid);
            userReactToCommentID.setUserId(uid);
            UserReactToComment userReactToComment = new UserReactToComment();
            userReactToComment.setId(userReactToCommentID);
            userReactToComment.setComment(comment);
            userReactToComment.setUser(user);
            userReactToComment.setReaction(reaction);
            util.userReactToCommentRepository.save(userReactToComment);


            return commentToCommentDTO(comment);
        }
        throw new BadRequestException("Id is not positive");
    }

    public CommentSimpleResponseDTO removeReaction(int cid, int uid) {
        if (cid > 0) {
            Comment comment = util.commentGetById(cid);
            UserReactToCommentID userReactToCommentID = new UserReactToCommentID();
            userReactToCommentID.setCommentId(cid);
            userReactToCommentID.setUserId(uid);
            Optional<UserReactToComment> optional = util.userReactToCommentRepository.findById(userReactToCommentID);
            if (optional.isPresent()) {
                util.userReactToCommentRepository.deleteById(userReactToCommentID);
                return commentToCommentDTO(comment);
            }
            throw new BadRequestException("You haven't reacted to this comment yet");
        }
        throw new BadRequestException("Id is not positive");
    }

    public CommentSimpleResponseDTO respondToComment(Comment comment, int uid, int cid) {
        String text = comment.getText();
        if (text.trim().isEmpty() || text.length() > COMMENT_TXT_MAX_LENGTH) {
            throw new BadRequestException("Invalid comment text!");
        }
        if (cid > 0) {
            Comment comment1 = util.commentGetById(cid);
            comment.setCreator(util.userGetById(uid));
            comment.setVideo(util.videoGetById(comment1.getVideo().getId()));
            comment.setCreationDate(LocalDateTime.now());
            util.commentRepository.save(comment);

            CommentHasCommentID commentHasCommentID = new CommentHasCommentID();
            commentHasCommentID.setParentId(cid);
            commentHasCommentID.setChildId(comment.getId());

            CommentHasComment commentHasComment = new CommentHasComment();
            commentHasComment.setId(commentHasCommentID);
            commentHasComment.setParent(comment1);
            commentHasComment.setChild(comment);

            util.commentHasCommentRepository.save(commentHasComment);
            return commentToCommentDTO(comment1);
        }
        throw new BadRequestException("Id is not positive");
    }


    private CommentSimpleResponseDTO commentToCommentDTO(Comment comment) {
        int likes = util.userReactToCommentRepository.findAllByReactionAndCommentId('+', comment.getId()).size();
        int dislikes = util.userReactToCommentRepository.findAllByReactionAndCommentId('-', comment.getId()).size();

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
        List<CommentHasComment> subComments = util.commentHasCommentRepository.findAllByParent(util.commentGetById(cid));
        for (CommentHasComment chc : subComments) {
            dtos.add(commentToCommentDTO(chc.getChild()));
        }
        return dtos;
    }

    public List<CommentSimpleResponseDTO> getAllCommentsOfVideo(int vid) {
        List<CommentSimpleResponseDTO> dtos = new ArrayList<>();
        Set<Comment> commentsOfVideos = util.videoGetById(vid).getComments();
        List<Comment> subComments = new ArrayList<>();
        for (Comment comment: commentsOfVideos) {
            Optional<CommentHasComment> chc = util.commentHasCommentRepository.findByChild(comment);
            if (chc.isPresent()){
                subComments.add(chc.get().getChild());
            }
        }
        subComments.forEach(commentsOfVideos::remove);
        for (Comment chc : commentsOfVideos) {
            dtos.add(commentToCommentDTO(chc));
        }
        return dtos;
    }
}
