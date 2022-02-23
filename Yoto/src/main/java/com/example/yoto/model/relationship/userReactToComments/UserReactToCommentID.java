package com.example.yoto.model.relationship.userReactToComments;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class UserReactToCommentID implements Serializable {

    @Column(name = "user_id")
    private int userId;
    @Column(name = "comment_id")
    private int commentId;

}
