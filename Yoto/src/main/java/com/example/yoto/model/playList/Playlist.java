package com.example.yoto.model.playList;


import com.example.yoto.model.user.User;
import com.example.yoto.model.video.Video;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
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

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "last_actualization")
    private LocalDateTime lastActualization;

    @Column(name = "is_private")
    private boolean isPrivate;

    @Column
    private String backgroundUrl;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "videos_in_playlists",
            joinColumns = {@JoinColumn(name = "playlist_id")},
            inverseJoinColumns = {@JoinColumn(name = "video_id")})
    private Set<Video> videos = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return id == playlist.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
