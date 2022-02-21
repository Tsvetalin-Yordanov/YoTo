
package com.example.yoto.model.user;

import com.example.yoto.model.category.Category;
import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.relationship.URTC.UserReactToComment;
import com.example.yoto.model.comment.Comment;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.yoto.model.relationship.URTV.UserReactToVideo;
import com.example.yoto.model.video.Video;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean verified;

    //set of published comments
    @OneToMany(mappedBy = "creator")
    private Set<Comment> comments;

    //set of categories user follows
    @ManyToMany(mappedBy = "followersOfCategory")
    private Set<Category> followedCategories;

    //set of published playlists
    @OneToMany(mappedBy = "creator")
    private Set<Playlist> playlists = new HashSet<>();

    //set of published videos
    @OneToMany(mappedBy = "user")
    private Set<Video> videos = new HashSet<>();

    //comments user has reacted to
    @OneToMany(mappedBy = "user")
    private Set<UserReactToComment> reactionsToComments = new HashSet<>();

    //videos user has reacted to
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserReactToVideo> reactedVideos = new HashSet<>();

    //following
    @ManyToMany(mappedBy = "observerUsers")
    private Set<User> publisherUsers = new HashSet<>();

    //followers
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "users_follow_users",
            joinColumns = {@JoinColumn(name = "publisher_id")},
            inverseJoinColumns = {@JoinColumn(name = "observer_id")})
    private Set<User> observerUsers = new HashSet<>();

    //watched videos history
    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Video> watchedVideos = new LinkedList<>();

}
