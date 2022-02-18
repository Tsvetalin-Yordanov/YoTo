package com.example.yoto.model.video;


import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.playList.PlayListRepository;
import com.example.yoto.model.relationship.URTV.UserReactToVideo;
import com.example.yoto.model.relationship.URTV.UserReactToVideoRepository;
import com.example.yoto.model.relationship.URTV.UsersReactToVideosId;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserReactToVideoRepository userReactToVideoRepository;
    @Autowired
    private PlayListRepository playListRepository;


    public Video getById(int id) {
        return videoGetById(id);
    }

    public Video uploadVideo(Video videoReq, int userId) {
        User user = userGetById(userId);
        videoReq.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Creator not found")));
        //TODO
        videoReq.setUploadDate(LocalDateTime.now());
        if (videoReq.getTitle() == null || videoReq.getTitle().isBlank()) {
            throw new BadRequestException("Title is mandatory");
        }
        if (videoReq.getUploadDate().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Invalid date and time");
        }
        if (videoReq.getVideoUrl() == null || videoReq.getVideoUrl().isBlank()) {
            throw new BadRequestException("No content to upload");
        }
        return videoRepository.save(videoReq);
    }

    public Video likeVideo(int vId, int userId) {
        return reactedVideo(vId, userId, '+');
    }

    public Video dislikeVideo(int vId, int userId) {
        return reactedVideo(vId, userId, '-');
    }

    private Video reactedVideo(int vId, int userId, char c) {
        User user = userGetById(userId);
        Video video = videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        UserReactToVideo userReactToVideo = new UserReactToVideo(usersReactToVideosId, user, video, c);
        userReactToVideoRepository.save(userReactToVideo);
        return video;
    }

    public Video removeReaction(int vId, int userId) {
        User user = userGetById(userId);
        Video video = videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        UserReactToVideo userReactToVideo = userReactToVideoRepository.findById(usersReactToVideosId)
                .orElseThrow(() -> new BadRequestException("You haven't reacted to this video yet"));
        userReactToVideoRepository.deleteById(usersReactToVideosId);
        return video;

    }
//TODO няма да бачка с user
    public int watch(int vId, int userId) {
        User user = userGetById(userId);
        Video video = videoGetById(vId);
        video.getUsers().add(user);
        videoRepository.save(video);
        return video.getUsers().size();
    }

    public Video videoGetById(int id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }

    public User userGetById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
