package com.example.yoto.model.relationship.CHC;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentHasCommentRepository extends JpaRepository<CommentHasComment,CommentHasCommentID> {

    Optional<CommentHasComment> findById(CommentHasCommentID key);
}