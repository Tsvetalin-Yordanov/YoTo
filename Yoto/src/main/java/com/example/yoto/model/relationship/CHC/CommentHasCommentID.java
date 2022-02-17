package com.example.yoto.model.relationship.CHC;


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
public class CommentHasCommentID implements Serializable {

    @Column(name ="parent_comment_id")
    private int parentId;
    @Column(name ="child_comment_id")
    private int childId;
}
