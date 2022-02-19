package com.example.yoto.model.relationship.URTC;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReactToCommentRepository extends JpaRepository<UserReactToComment, UserReactToCommentID> {

    Optional<UserReactToComment> findById(UserReactToCommentID key);
    List<UserReactToComment> findAllByReactionAndCommentId(char reaction , int commentId);
}
