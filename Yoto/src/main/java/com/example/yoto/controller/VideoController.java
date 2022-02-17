package com.example.yoto.controller;

import com.example.yoto.model.relationship.URTV.UserReactToVideoService;
import com.example.yoto.model.user.UserService;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoResponseDTO;
import com.example.yoto.model.video.VideoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserReactToVideoService userReactToVideoService;


    @GetMapping("/videos/{id:[\\d]+}")
    public VideoResponseDTO getById(@PathVariable int id) {
        Video video = videoService.getById(id);
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return vDto;
    }

    @PostMapping("/videos/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<VideoResponseDTO> upload(@RequestBody Video videoReq,HttpSession session, HttpServletRequest request) {
        //TODO Valid session??
        userService.validateLogin(session,request);
        Video video = videoService.uploadVideo(videoReq);
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(201).body(vDto);
    }

    @PutMapping("/videos/like")
    public ResponseEntity<VideoResponseDTO> liked(@RequestParam int videoId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        Video video = userReactToVideoService.likeVideo(videoId,(int)session.getAttribute("user_id"));
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(201).body(vDto);
    }


    @PutMapping("/videos/dislike")
    public ResponseEntity<VideoResponseDTO> dislike(@RequestParam int videoId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        Video video = userReactToVideoService.dislikeVideo(videoId,(int)session.getAttribute("user_id"));
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(201).body(vDto);
    }

    @DeleteMapping("/videos/remove_reaction")
    public ResponseEntity<VideoResponseDTO> removeReaction(@RequestParam int videoId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        Video video = userReactToVideoService.removeReaction(videoId,(int)session.getAttribute("user_id"));
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(200).body(vDto);
    }


}
