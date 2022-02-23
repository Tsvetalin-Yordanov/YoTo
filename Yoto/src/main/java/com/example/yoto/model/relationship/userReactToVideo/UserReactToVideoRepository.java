package com.example.yoto.model.relationship.userReactToVideo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReactToVideoRepository extends JpaRepository<UserReactToVideo, UsersReactToVideosId> {

    List<UserReactToVideo> findAllByVideoIdAndReaction(int videoId, char reaction);

}
