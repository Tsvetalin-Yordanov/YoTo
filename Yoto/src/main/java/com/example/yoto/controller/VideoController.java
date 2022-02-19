package com.example.yoto.controller;

import com.example.yoto.model.user.UserService;
import com.example.yoto.model.video.*;
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

    @GetMapping("/videos/{id:[\\d]+}")
    public VideoComplexResponseDTO getById(@PathVariable int id) {
        VideoComplexResponseDTO video = videoService.getById(id);
        return video;
    }

    @PostMapping("/videos/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<VideoSimpleResponseDTO> upload(@RequestBody Video videoReq,HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session,request);
        VideoSimpleResponseDTO vDto = videoService.uploadVideo(videoReq,(int)session.getAttribute(USER_ID));
        return ResponseEntity.status(201).body(vDto);
    }

    @PutMapping("/videos/like")
    public ResponseEntity<VideoComplexResponseDTO> liked(@RequestParam int vId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        VideoComplexResponseDTO vDto = videoService.likeVideo(vId,(int)session.getAttribute(USER_ID));
        return ResponseEntity.status(201).body(vDto);
    }


    @PutMapping("/videos/dislike")
    public ResponseEntity<VideoComplexResponseDTO> dislike(@RequestParam int vId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        VideoComplexResponseDTO vDto = videoService.dislikeVideo(vId,(int)session.getAttribute(USER_ID));
        return ResponseEntity.status(201).body(vDto);
    }

    @DeleteMapping("/videos/remove_reaction")
    public ResponseEntity<VideoComplexResponseDTO> removeReaction(@RequestParam int vId,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        VideoComplexResponseDTO vDto = videoService.removeReaction(vId,(int)session.getAttribute(USER_ID));
        return ResponseEntity.status(200).body(vDto);
    }

    @PostMapping("/videos/watch")
    public int  watch(@RequestParam int vId,HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        return videoService.watch(vId, (int) session.getAttribute(USER_ID));
    }

}
