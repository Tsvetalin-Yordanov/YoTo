package com.example.yoto.model.relationship;

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
    private Integer user_id;
    @Column(name = "video_id")
    private Integer video_id;


    public UsersReactToVideosId() {
    }

    public UsersReactToVideosId(Integer user_id, Integer video_id) {
        this.user_id = user_id;
        this.video_id = video_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersReactToVideosId that = (UsersReactToVideosId) o;
        return user_id.equals(that.user_id) && video_id.equals(that.video_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, video_id);
    }
}