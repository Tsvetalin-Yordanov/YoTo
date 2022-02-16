
package com.example.yoto.model.user;

import com.example.yoto.model.manyToManyTables.UserReactToComment;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.relationship.UserReactVideo;
import model.video.Video;
import com.example.yoto.model.video.Video;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String phoneNumber;
    @Column
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfBirth;
    @Column
    private String aboutMe;
    @Column
    private char gender;
    @Column
    private String profileImageUrl;
    @Column
    private String backgroundImageUrl;

    private Set<UserReactVideo> userReactVideo = new HashSet<>();
    @OneToMany(mappedBy = "primaryKey", cascade = CascadeType.ALL)
    public Set<UserReactVideo> getUserReactVideo() {
        return userReactVideo;
    }

    //ManyToMany
   // private Set<UserReactToComment> userReactToComment = new HashSet<UserReactToComment>();


    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "users_search_videos", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "video_id")})
    private Set<Video> videos = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "users_follow_users",joinColumns = {@JoinColumn(name = "observer_id")},inverseJoinColumns = {@JoinColumn(name = "publisher_id")})
    private Set<User> observerUsers = new HashSet<>();

    @ManyToMany(mappedBy = "observerUsers")
    private Set<User> publisherUsers = new HashSet<>();

//    @OneToMany(mappedBy = "primaryKey.user",cascade = CascadeType.ALL)
//    public Set<UserReactToComment> getUserReactToComment(){
//        return userReactToComment;
//    }
//
//    public void setUserReactToComment(Set<UserReactToComment> comment) {
//        this.userReactToComment = comment;
//    }
//
//    public void addUserReactToComment(UserReactToComment comment) {
//        this.userReactToComment.add(comment);
//    }
}
