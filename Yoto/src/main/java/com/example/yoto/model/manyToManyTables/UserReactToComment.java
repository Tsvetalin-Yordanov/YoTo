package com.example.yoto.model.manyToManyTables;

import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.user.User;

import javax.persistence.*;

@Entity
@Table(name = "users_react_to_comments")
@AssociationOverrides({@AssociationOverride(name = "primaryKey.user",joinColumns = @JoinColumn(name = "user_id")),
                        @AssociationOverride(name = "primaryKey.comment",joinColumns = @JoinColumn(name = "comment_id"))})
public class UserReactToComment {

    private UserReactToCommentID primaryKey = new UserReactToCommentID();
    private char reaction;

    @EmbeddedId
    public UserReactToCommentID getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(UserReactToCommentID primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Transient
    public User getUser() {
        return getPrimaryKey().getUser();
    }

    public void setUser(User user) {
        getPrimaryKey().setUser(user);
    }

    @Transient
    public Comment getComment() {
        return getPrimaryKey().getComment();
    }

    public void setGroup(Comment comment) {
        getPrimaryKey().setComment(comment);
    }

    public void setReaction(char reaction) {
        this.reaction = reaction;
    }

    public char getReaction() {
        return reaction;
    }
}
