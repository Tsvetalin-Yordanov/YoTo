package com.example.yoto.model.relationship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReactToVideoRepository extends JpaRepository<UserReactToVideo , UsersReactToVideosId> {


}
