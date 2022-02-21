package com.example.yoto.model.video;

import com.example.yoto.model.category.Category;
import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.yoto.model.relationship.URTV.UserReactToVideo;
//import org.hibernate.annotations.NaturalIdCache
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "videos")
//@NaturalIdCache
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title")
    private String title;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
    @Column(name = "video_url")
    private String videoUrl;
    @Column(name = "is_private",nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isPrivate;

    //set of comments of the video
    @OneToMany(mappedBy = "video")
    private Set<Comment> comments;

    //set of categories of the video
    @ManyToMany(mappedBy = "videosInCategory")
    private Set<Category> categoriesOfVideo;

    //set of people who reacted
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<UserReactToVideo> reactedUsers = new HashSet<>();

    //set of users who have watched this video
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "users_watched_videos",
            joinColumns = {@JoinColumn(name = "video_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> users = new LinkedList<>();

    //set of playlist with this video
    @ManyToMany(mappedBy = "videos")
    private Set<Playlist> playlists = new HashSet<>();

}
