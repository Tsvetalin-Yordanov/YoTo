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
        if (id > 0) {
            Optional<Video> optionalVideo = videoRepository.findById(id);
            if (optionalVideo.isPresent()) {
                return optionalVideo.get();
            }
            throw new NotFoundException("Video not found");
        }
        throw new BadRequestException("Id is not positive");
    }


    public Video uploadVideo(Video videoReq,int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (videoReq.getTitle() == null || videoReq.getTitle().isBlank()) {
            throw new BadRequestException("Title is mandatory");
        }
        if(videoReq.getUploadDate().isAfter(LocalDateTime.now())) {
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
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            Optional<Video> videoOpt = videoRepository.findById(vId);
            if (videoOpt.isPresent()) {
                UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
                UserReactToVideo userReactToVideo = new UserReactToVideo(usersReactToVideosId, userOpt.get(), videoOpt.get(), c);
                userReactToVideoRepository.save(userReactToVideo);
                return videoOpt.get();
            } else {
                throw new NotFoundException("Video not found");
            }
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public Video removeReaction(int vId, int userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            Optional<Video> videoOpt = videoRepository.findById(vId);
            if (videoOpt.isPresent()) {
                UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
                Optional<UserReactToVideo> userReactToVideo = userReactToVideoRepository.findById(usersReactToVideosId);
                if (userReactToVideo.isPresent()) {
                    userReactToVideoRepository.deleteById(usersReactToVideosId);
                    return videoOpt.get();
                } else {
                    throw new BadRequestException("You haven't reacted to this comment yet");
                }
            } else {
                throw new NotFoundException("Video not found");
            }
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public int watch(int vId, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Video video = videoRepository.findById(vId).orElseThrow(()-> new NotFoundException("Video not found"));
        if(!user.getWatchedVideos().contains(video)){
            video.getUsers().add(user);
            videoRepository.save(video);
        }
        //TODO ??
        return video.getUsers().size();
    }

    public int addToPlaylist(int vId, int pLId,int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Video video = videoRepository.findById(vId).orElseThrow(()-> new NotFoundException("Video not found"));
        Playlist playlist = playListRepository.findById(pLId).orElseThrow(()-> new NotFoundException("Playlist not found"));
        if(playlist.getVideos().contains(video)){
            //TODO
            throw new BadRequestException("Video is in playlist");
        }
        playlist.getVideos().add(video);
        videoRepository.save(video);
        return video.getPlaylists().size();
    }

    public int deleteFromPlaylist(int vId, int pLId, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Video video = videoRepository.findById(vId).orElseThrow(()-> new NotFoundException("Video not found"));
        Playlist playlist = playListRepository.findById(pLId).orElseThrow(()-> new NotFoundException("Playlist not found"));
        if(!playlist.getVideos().contains(video)){
            //TODO
            throw new BadRequestException("Video is not in playlist");
        }
        video.getPlaylists().remove(playlist);
        videoRepository.save(video);
        return video.getPlaylists().size();
    }
}
