package com.example.yoto.model.relationship;


import com.example.yoto.model.user.User;
import com.example.yoto.model.video.Video;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users_react_to_videos")
@Getter
@Setter
public class UserReactVideo {


    @EmbeddedId
    private UsersReactToVideosId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("video_id")
    private Video video;


    @Column(name = "reaction")
    private char reaction;

    public UserReactVideo() {}

    public UserReactVideo(User user, Video video) {
        this.user = user;
        this.video = video;
        this.id = new UsersReactToVideosId(user.getId(), video.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReactVideo that = (UserReactVideo) o;
        return Objects.equals(user, that.user) && Objects.equals(video, that.video);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, video);
    }
}