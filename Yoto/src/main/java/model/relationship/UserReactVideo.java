package model.relationship;

import lombok.Getter;
import lombok.Setter;
import model.user.User;
import model.video.Video;

import javax.persistence.*;

@Entity
@Table(name = "users_react_to_videos")
@AssociationOverrides({
        @AssociationOverride(name = "primaryKey.user",
                joinColumns = @JoinColumn(name = "id")),
        @AssociationOverride(name = "primaryKey.video",
                joinColumns = @JoinColumn(name = "id"))})
public class UserReactVideo {


    @Embedded
    private UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId();

    private char reaction;

    public UsersReactToVideosId getUsersReactToVideosId() {
        return usersReactToVideosId;
    }

    @EmbeddedId
    public UsersReactToVideosId getPrimaryKey(){
        return usersReactToVideosId;
    }

    public void setPrimaryKey(UsersReactToVideosId primaryKey) {
        this.usersReactToVideosId = primaryKey;
    }
    @Transient
    public User getUser(){
       return getPrimaryKey().getUser();
    }
    public void setUser(User user) {
        getPrimaryKey().setUser(user);
    }
    @Transient
    public Video getVideo() {
        return getPrimaryKey().getVideo();
    }

    public void setGroup(Video group) {
        getPrimaryKey().setVideo(group);
    }

    public char getReaction() {
        return reaction;
    }

    public void setReaction(char reaction) {
        this.reaction = reaction;
    }
}