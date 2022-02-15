package com.example.yoto.model.manyToManyTables;

import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
@Embeddable
public class UserReactToCommentID implements Serializable {
    private User user;
    private Comment comment;

    @ManyToOne(cascade = CascadeType.ALL)
    public User getUser(){
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public Comment getComment(){
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
