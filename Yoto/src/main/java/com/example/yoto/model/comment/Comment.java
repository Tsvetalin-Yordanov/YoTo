package com.example.yoto.model.comment;

import com.example.yoto.model.relationship.CHC.CommentHasComment;
import com.example.yoto.model.relationship.URTC.UserReactToComment;
import com.example.yoto.model.user.User;
import com.example.yoto.model.video.Video;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "text")
    private String text;

    @Column(name = "create_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime creationDate;

    //set of user who reacted to comment
    @OneToMany(mappedBy = "comment")
    private Set<UserReactToComment> reactionsOfUsers = new HashSet<>();

    //child comments
    @OneToMany(mappedBy = "parent")
    private Set<CommentHasComment> subComments = new HashSet<>();

    //parent comments
    @OneToMany(mappedBy = "child")
    private Set<CommentHasComment> superComment = new HashSet<>();
}
