package com.example.yoto.model.video;


import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.playList.PlayListRepository;
import com.example.yoto.model.relationship.URTV.UserReactToVideo;
import com.example.yoto.model.relationship.URTV.UserReactToVideoRepository;
import com.example.yoto.model.relationship.URTV.UsersReactToVideosId;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;

import com.example.yoto.model.user.UserService;

import com.example.yoto.model.user.UserSimpleResponseDTO;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;


@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserReactToVideoRepository userReactToVideoRepository;
    @Autowired
    private ModelMapper modelMapper;


    public VideoComplexResponseDTO getById(int id) {
        Video video = videoGetById(id);
        return videoToComplexDTO(video);
    }

    public VideoSimpleResponseDTO uploadVideo(Video videoReq, int userId) {
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
        Video video = videoRepository.save(videoReq);
        return videoToSimpleDTO(video);
    }

    public VideoComplexResponseDTO likeVideo(int vId, int userId) {
        Video video = reactedVideo(vId, userId, '+');
        return videoToComplexDTO(video);
    }

    public VideoComplexResponseDTO dislikeVideo(int vId, int userId) {
        Video video = reactedVideo(vId, userId, '-');
        return videoToComplexDTO(video);
    }

    private Video reactedVideo(int vId, int userId, char c) {
        User user = userGetById(userId);
        Video video = videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        UserReactToVideo userReactToVideo = new UserReactToVideo(usersReactToVideosId, user, video, c);
        userReactToVideoRepository.save(userReactToVideo);
        return video;
    }

    public VideoComplexResponseDTO removeReaction(int vId, int userId) {
        User user = userGetById(userId);
        Video video = videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        UserReactToVideo userReactToVideo = userReactToVideoRepository.findById(usersReactToVideosId)
                .orElseThrow(() -> new BadRequestException("You haven't reacted to this video yet"));
        userReactToVideoRepository.deleteById(usersReactToVideosId);
        return videoToComplexDTO(video);
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

    public static VideoSimpleResponseDTO videoToSimpleDTO(Video video) {
        VideoSimpleResponseDTO vDto = new VideoSimpleResponseDTO();
        vDto.setId(video.getId());
        vDto.setTitle(video.getTitle());
        vDto.setUser(UserService.userToSimpleDTO(video.getUser()));
        vDto.setUploadDate(video.getUploadDate());
        vDto.setVideoUrl(video.getVideoUrl());
        vDto.setViews(video.getUsers().size());
        return vDto;
    }

    public VideoComplexResponseDTO videoToComplexDTO(Video video) {
        VideoComplexResponseDTO vDto = new VideoComplexResponseDTO();
        vDto.setId(video.getId());
        vDto.setTitle(video.getTitle());
        vDto.setUser(UserService.userToSimpleDTO(video.getUser()));
        vDto.setUploadDate(video.getUploadDate());
        vDto.setVideoUrl(video.getVideoUrl());
        vDto.setPrivate(video.isPrivate());
        vDto.setViews(video.getUsers().size());
        vDto.setLikes(userReactToVideoRepository.findAllByVideoIdAndReaction(video.getId(), '+').size());
        vDto.setDislikes(userReactToVideoRepository.findAllByVideoIdAndReaction(video.getId(), '-').size());
        return vDto;
    }

    @SneakyThrows
    public String uploadVideoImage(int vId,MultipartFile file, int user_id) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        Video video = videoGetById(vId);
        String videoTitle = video.getTitle();
        String fileName = videoTitle + "&" + System.nanoTime() + "." + fileExtension;
        Files.copy(file.getInputStream(), new File("uploads"+File.separator+fileName).toPath());
        video.setVideoUrl(fileName);
        videoRepository.save(video);
        return fileName;
    }

}
