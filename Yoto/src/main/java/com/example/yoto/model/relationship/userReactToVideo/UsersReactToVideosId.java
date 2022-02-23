package com.example.yoto.model.relationship.userReactToVideo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class UsersReactToVideosId implements Serializable {

    @Column(name = "user_id")
    private int userId;
    @Column(name = "video_id")
    private int videoId;


    public UsersReactToVideosId() {}

    public UsersReactToVideosId(int user_id,int video_id){
        this.userId = user_id;
        this.videoId = video_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersReactToVideosId that = (UsersReactToVideosId) o;
        return userId == that.userId && videoId == that.videoId;

    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, videoId);
    }

}

