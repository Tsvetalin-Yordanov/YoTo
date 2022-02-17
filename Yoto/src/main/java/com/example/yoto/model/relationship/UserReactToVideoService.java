package com.example.yoto.model.relationship;

import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserReactToVideoService {

    @Autowired
    private UserReactToVideoRepository userReactToVideoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;

    public Video likeVideo(int videoId, int userId) {
        return reactedVideo(videoId,userId,'+');
    }

    public Video dislikeVideo(int videoId, int userId) {
        return reactedVideo(videoId,userId,'-');
    }


    //TODO
    public Video deleteLikeVideo(int videoId, int userId) {
        return null;
    }


    private Video reactedVideo(int videoId, int userId, char c) {
        Optional<User> userOpt = userRepository.findById(userId);
//        if (userOpt.isPresent()) {
            Optional<Video> videoOpt = videoRepository.findById(videoId);
            if (videoOpt.isPresent()) {
                UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, videoId);
                UserReactToVideo userReactToVideo = new UserReactToVideo(usersReactToVideosId, userOpt.get(), videoOpt.get(), '+');
                userReactToVideoRepository.save(userReactToVideo);
                return videoOpt.get();
            } else {
                throw new NotFoundException("Video not found");
            }
//        } else {
//            throw new NotFoundException("User bot found");
//        }
    }

}
