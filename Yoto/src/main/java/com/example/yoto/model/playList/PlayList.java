package com.example.yoto.model.playList;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "playlists")
@Setter
@Getter
@NoArgsConstructor
public class PlayList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String title;
    @Column
    private int creatorId;
    @Column
    private LocalDateTime createDate;
    @Column
    private LocalDateTime lastActualization;
    @Column
    private boolean isPrivate;

//    @ManyToMany(mappedBy = "playLists")
//    private Set<Video> videos = new HashSet<>();
}
