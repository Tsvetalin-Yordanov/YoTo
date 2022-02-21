package com.example.yoto.model.playList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayListRepository extends JpaRepository<Playlist,Integer> {

    List<Playlist> findAllByTitleContainsAndIsPrivate(String title,Boolean isPrivate);
    List<Playlist> findAllByCreatorIdAndIsPrivate(Integer creatorId,Boolean isPrivate);

}