package com.example.yoto.model.video;
import com.example.yoto.model.playList.PlayList;
import com.example.yoto.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String title;
    @Column
    private int userId;
    @Column
    private LocalDateTime uploadDate;
    @Column
    private String videoUrl;
    @Column
    private boolean isPrivate;
    @ManyToMany(mappedBy = "videos")
    private Set<User> users = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "video_in_playlists",
            joinColumns = {@JoinColumn(name = "video_id")},inverseJoinColumns = {@JoinColumn(name = "playlist_id")})
    private Set<PlayList> playLists = new HashSet<>();

}
