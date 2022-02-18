package com.example.yoto.controller;
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
import static com.example.yoto.model.user.UserService.USER_ID;

@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/videos/{id:[\\d]+}")
    public VideoResponseDTO getById(@PathVariable int id) {
        Video video = videoService.getById(id);
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return vDto;
    }

    @PostMapping("/videos/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<VideoResponseDTO> upload(@RequestBody Video videoReq,HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session,request);
        Video video = videoService.uploadVideo(videoReq,(int)session.getAttribute(USER_ID));
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(201).body(vDto);
    }

    @PutMapping("/videos/like")
    public ResponseEntity<VideoResponseDTO> liked(@RequestParam int vId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        Video video = videoService.likeVideo(vId,(int)session.getAttribute(USER_ID));
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(201).body(vDto);
    }


    @PutMapping("/videos/dislike")
    public ResponseEntity<VideoResponseDTO> dislike(@RequestParam int vId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        Video video = videoService.dislikeVideo(vId,(int)session.getAttribute(USER_ID));
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(201).body(vDto);
    }

    @DeleteMapping("/videos/remove_reaction")
    public ResponseEntity<VideoResponseDTO> removeReaction(@RequestParam int vId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        Video video = videoService.removeReaction(vId,(int)session.getAttribute(USER_ID));
        VideoResponseDTO vDto = modelMapper.map(video, VideoResponseDTO.class);
        return ResponseEntity.status(200).body(vDto);
    }
    @PostMapping("/videos/watch")
    public int  watch(@RequestParam int vId,HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        return videoService.watch(vId, (int) session.getAttribute(USER_ID));
    }

}
