package com.example.yoto.model.comment;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VideoRepository videoRepository;

    public Comment createComment(Comment comment, int uid, int vid) {
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new BadRequestException("text is mandatory");
        }
        comment.setCreatorId(uid);
        Optional<Video> opt = videoRepository.findById(vid);
        if (!opt.isPresent()){
            throw new NotFoundException("Cant comment nonexistent video");
        }
        comment.setVideoId(vid);
        comment.setCreationDate(LocalDate.now());
        commentRepository.save(comment);

        return comment;
    }

    public Comment editComment(Comment comment) {
        Optional<Comment> opt = commentRepository.findById(comment.getId());
        if (opt.isPresent()) {
            if (comment.getText() == null || comment.getText().isBlank()) {
                throw new BadRequestException("text is mandatory");
            }
            commentRepository.save(comment);
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
}
