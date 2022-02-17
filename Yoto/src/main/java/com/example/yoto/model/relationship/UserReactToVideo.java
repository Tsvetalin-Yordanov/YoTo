package com.example.yoto.model.relationship;

import com.example.yoto.model.user.User;
import com.example.yoto.model.video.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users_react_to_videos")
@Getter
@Setter
public class UserReactToVideo {


    @EmbeddedId
    private UsersReactToVideosId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    @JoinColumn(name = "video_id")
    private Video video;


    @Column(name = "reaction")
    private char reaction;

    public UserReactToVideo(UsersReactToVideosId id, User user, Video video, char reaction) {
        this.id = id;
        this.user = user;
        this.video = video;
        this.reaction = reaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReactToVideo that = (UserReactToVideo) o;
        return Objects.equals(user, that.user) && Objects.equals(video, that.video);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, video);
    }
}
