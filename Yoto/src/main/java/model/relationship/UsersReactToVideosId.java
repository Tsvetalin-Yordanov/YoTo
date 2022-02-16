package model.relationship;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.user.User;
import model.video.Video;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class UsersReactToVideosId implements Serializable {

    private User user;
    private Video video;

    @ManyToOne(cascade = CascadeType.ALL)
    public User getUser(){
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    public Video getVideo(){
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
