package com.example.yoto.model.relationship.userReactToComments;

import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users_react_to_comments")
public class UserReactToComment {

    @EmbeddedId
    private UserReactToCommentID id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private char reaction;

}
