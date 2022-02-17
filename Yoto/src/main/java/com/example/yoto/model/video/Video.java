package com.example.yoto.model.video;
import com.example.yoto.model.playList.PlayList;
import com.example.yoto.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.yoto.model.relationship.URTV.UserReactToVideo;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.NaturalIdCache;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
@NaturalIdCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column
    private LocalDateTime uploadDate;
    @Column
    private String videoUrl;
    @Column
    private boolean isPrivate;

    @OneToMany(mappedBy = "video")
    @JsonManagedReference
    private Set<Comment> comments;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<UserReactToVideo> reactedUsers = new HashSet<>();


    @ManyToMany(mappedBy = "watchedVideos",cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "video_in_playlists",
            joinColumns = {@JoinColumn(name = "video_id")},inverseJoinColumns = {@JoinColumn(name = "playlist_id")})
    private Set<PlayList> playLists = new HashSet<>();







}
