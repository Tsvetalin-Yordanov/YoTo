package com.example.yoto.model.playList;


import com.example.yoto.model.user.User;
import com.example.yoto.model.video.Video;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "playlists")
@Setter
@Getter
@NoArgsConstructor
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column
    private LocalDateTime createDate;

    @Column
    private LocalDateTime lastActualization;

    @Column
    private boolean isPrivate;

    @ManyToMany(mappedBy = "playlists")
    private Set<Video> videos = new HashSet<>();
}
