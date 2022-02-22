package com.example.yoto.model.video;

import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findAllByTitleContainsAndIsPrivate(String title, boolean isPrivate);

    List<Video> findAllByIsPrivate(boolean isPrivate);


    List<Video> findAllByUserIdAndIsPrivate(Integer userId, Boolean isPrivate);

    List<Video> findAllByOrderByUploadDateDesc();

    List<Video> findAllByOrderByUploadDateAsc();

}