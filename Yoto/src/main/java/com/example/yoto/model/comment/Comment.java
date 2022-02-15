package com.example.yoto.model.comment;

import com.example.yoto.model.manyToManyTables.UserReactToComment;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
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
    @Column(name = "creator_id")
    private int creatorId;
    @Column(name = "video_id")
    private int videoId;
    @Column(name = "text")
    private String text;
    @Column(name = "create_date")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate creationDate;

    //ManyToMany
    private Set<UserReactToComment> userReactToComment = new HashSet<UserReactToComment>();

    @OneToMany(mappedBy = "primaryKey.comment",
            cascade = CascadeType.ALL)
    public Set<UserReactToComment> getUserReactToComment() {
        return userReactToComment;
    }

    public void setUserReactToComment(Set<UserReactToComment> users) {
        this.userReactToComment = users;
    }

    public void addUserReactToComment(UserReactToComment users) {
        this.userReactToComment.add(users);
    }
}
