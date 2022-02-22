package com.example.yoto.model.relationship.CHC;

import com.example.yoto.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentHasCommentRepository extends JpaRepository<CommentHasComment,CommentHasCommentID> {

    Optional<CommentHasComment> findById(CommentHasCommentID key);
    List<CommentHasComment> findAllByParent(Comment parent);
    Optional<CommentHasComment> findByChild(Comment child);
}
