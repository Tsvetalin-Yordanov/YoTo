package com.example.yoto.model.relationship.chc;


import com.example.yoto.model.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "comments_have_comments")
public class CommentHasComment {

    @EmbeddedId
    private CommentHasCommentID id;

    @ManyToOne
    @MapsId("parentId")
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @ManyToOne
    @MapsId("childId")
    @JoinColumn(name = "child_comment_id")
    private Comment child;
}
