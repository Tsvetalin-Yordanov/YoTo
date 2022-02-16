package com.example.yoto.model.comment;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.relationship.CHC.CommentHasComment;
import com.example.yoto.model.relationship.CHC.CommentHasCommentID;
import com.example.yoto.model.relationship.CHC.CommentHasCommentRepository;
import com.example.yoto.model.relationship.URTC.UserReactToComment;
import com.example.yoto.model.relationship.URTC.UserReactToCommentID;
import com.example.yoto.model.relationship.URTC.UserReactToCommentRepository;
import com.example.yoto.model.relationship.UserReactVideo;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public Comment createComment(Comment comment, int uid, int vid) {
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new BadRequestException("text is mandatory");
        }
        Optional<Video> opt = videoRepository.findById(vid);
        if (opt.isPresent()) {
            comment.setCreator(userRepository.findById(uid).orElseThrow(() -> new NotFoundException("Creator not found")));
            comment.setVideoId(vid);
            //comment.setVideo(videoRepository.findById(vid).orElseThrow(() -> new NotFoundException("Video not found")));
            comment.setCreationDate(LocalDateTime.now());
            commentRepository.save(comment);
            return comment;
        }
        throw new NotFoundException("Video not found");
    }

    public Comment editComment(Comment comment) {
        Optional<Comment> opt = commentRepository.findById(comment.getId());
        if (opt.isPresent()) {
            if (comment.getText() == null || comment.getText().isBlank()) {
                throw new BadRequestException("text is mandatory");
            }
            opt.get().setText(comment.getText());
            opt.get().setCreationDate(LocalDateTime.now());
            commentRepository.save(opt.get());
        } else {
            throw new NotFoundException("Comment not found");
        }
        return comment;
    }

    public Comment deleteById(int id) {
        if (id > 0) {
            Comment comment = getById(id);
            commentRepository.deleteById(id);
            return comment;
        }
        throw new BadRequestException("Id is not positive");
    }

    public Comment getById(int id) {
        if (id > 0) {
            Optional<Comment> optionalComment = commentRepository.findById(id);
            if (optionalComment.isPresent()) {
                return optionalComment.get();
            }
            throw new NotFoundException("Comment not found");
        }
        throw new BadRequestException("Id is not positive");
    }

    public Comment likeComment(int cid, int uid) {
        Comment comment = reactToComment(cid, uid, '+');
        return comment;
    }

    public Comment dislikeComment(int cid, int uid) {
        Comment comment = reactToComment(cid, uid, '-');
        return comment;
    }

    public Comment reactToComment(int cid, int uid, char reaction) {
        if (cid > 0) {
            Optional<Comment> optionalComment = commentRepository.findById(cid);
            Optional<User> optionalUser = userRepository.findById(uid);
            if (optionalComment.isPresent()) {
                UserReactToCommentID userReactToCommentID = new UserReactToCommentID();
                userReactToCommentID.setCommentId(cid);
                userReactToCommentID.setUserId(uid);
                UserReactToComment userReactToComment = new UserReactToComment();
                userReactToComment.setId(userReactToCommentID);
                userReactToComment.setComment(optionalComment.get());
                userReactToComment.setUser(optionalUser.get());
                userReactToComment.setReaction(reaction);
                userReactToCommentRepository.save(userReactToComment);
                System.out.println(optionalComment.get().getReactionsOfUsers());
                return optionalComment.get();
            }
            throw new NotFoundException("Comment not found");
        }
        throw new BadRequestException("Id is not positive");
    }

    public Comment removeReaction(int cid, int uid) {
        if (cid > 0) {
            Optional<Comment> optionalComment = commentRepository.findById(cid);
            if (optionalComment.isPresent()) {
                UserReactToCommentID userReactToCommentID = new UserReactToCommentID();
                userReactToCommentID.setCommentId(cid);
                userReactToCommentID.setUserId(uid);
                Optional<UserReactToComment> optional = userReactToCommentRepository.findById(userReactToCommentID);
                if (optional.isPresent()) {
                    userReactToCommentRepository.deleteById(userReactToCommentID);
                    return optionalComment.get();
                }
                throw new BadRequestException("You haven't reacted to this comment yet");
            }
            throw new NotFoundException("Comment not found");
        }
        throw new BadRequestException("Id is not positive");
    }

    public Comment respondToComment(Comment comment, int uid, int cid) {
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new BadRequestException("text is mandatory");
        }
        if (cid > 0) {
            Optional<Comment> opt = commentRepository.findById(cid);
            if (opt.isPresent()) {
                   comment.setCreator(userRepository.findById(uid).orElseThrow(() -> new NotFoundException("Creator not found")));
                   comment.setVideoId(opt.get().getVideoId());
                   //comment.setVideo(videoRepository.findById(vid).orElseThrow(() -> new NotFoundException("Video not found")));
                   comment.setCreationDate(LocalDateTime.now());
                   commentRepository.save(comment);


                   CommentHasCommentID commentHasCommentID = new CommentHasCommentID();
                   commentHasCommentID.setParentId(cid);
                   commentHasCommentID.setChildId(comment.getId());

                   CommentHasComment commentHasComment = new CommentHasComment();
                   commentHasComment.setId(commentHasCommentID);
                   commentHasComment.setParent(opt.get());
                   commentHasComment.setChild(comment);

                   commentHasCommentRepository.save(commentHasComment);
                   return comment;
            }
            throw new NotFoundException("Comment not found");
        }
        throw new BadRequestException("Id is not positive");
    }
}
