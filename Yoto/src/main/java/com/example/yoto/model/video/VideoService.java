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
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return getVideoComplexDtoWithParameters(video);
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
        return getVideoSimpleDtoWithParameters(video);
    }

    public VideoComplexResponseDTO likeVideo(int vId, int userId) {
        Video video = reactedVideo(vId, userId, '+');
        return getVideoComplexDtoWithParameters(video);
    }

    public VideoComplexResponseDTO dislikeVideo(int vId, int userId) {
        Video video = reactedVideo(vId, userId, '-');
        return getVideoComplexDtoWithParameters(video);
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
        return getVideoComplexDtoWithParameters(video);
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
        VideoSimpleResponseDTO videoDTO = new VideoSimpleResponseDTO();
        videoDTO.setId(video.getId());
        videoDTO.setTitle(video.getTitle());
        videoDTO.setUser(UserService.userToSimpleDTO(video.getUser()));
        videoDTO.setUploadDate(video.getUploadDate());
        videoDTO.setVideoUrl(video.getVideoUrl());
        videoDTO.setViews(video.getUsers().size());
        return videoDTO;
    }

    private VideoComplexResponseDTO getVideoComplexDtoWithParameters(Video video) {
        VideoComplexResponseDTO vDto = modelMapper.map(video, VideoComplexResponseDTO.class);
        UserSimpleResponseDTO userDto = modelMapper.map(video.getUser(), UserSimpleResponseDTO.class);
        vDto.setUser(userDto);
        vDto.setViews(video.getUsers().size());
        vDto.setLikes(userReactToVideoRepository.findAllByVideoIdAndReaction(video.getId(), '+').size());
        vDto.setDislikes(userReactToVideoRepository.findAllByVideoIdAndReaction(video.getId(), '-').size());
        return vDto;
    }

    private VideoSimpleResponseDTO getVideoSimpleDtoWithParameters(Video video) {
        VideoSimpleResponseDTO vDto = modelMapper.map(video, VideoSimpleResponseDTO.class);
        UserSimpleResponseDTO userDto = modelMapper.map(video.getUser(), UserSimpleResponseDTO.class);
        vDto.setUser(userDto);
        vDto.setViews(video.getUsers().size());
        return vDto;
    }
}
