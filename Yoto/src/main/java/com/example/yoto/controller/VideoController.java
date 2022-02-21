package com.example.yoto.controller;

import com.example.yoto.model.user.UserService;
import com.example.yoto.model.video.*;
import com.example.yoto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.example.yoto.util.Util.USER_ID;


@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private Util util;

    @GetMapping("/videos/{id:[\\d]+}")
    public VideoComplexResponseDTO getById(@PathVariable int id) {
        VideoComplexResponseDTO video = videoService.getById(id);
        return video;
    }

    @PostMapping("/videos/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<VideoSimpleResponseDTO> upload(@RequestBody Video videoReq,HttpServletRequest request) {
        util.validateLogin(request);
        VideoSimpleResponseDTO vDto = videoService.uploadVideo(videoReq,util.getUserIdFromRequest(request));
        return ResponseEntity.status(201).body(vDto);
    }

    @PutMapping("/videos/like")
    public ResponseEntity<VideoComplexResponseDTO> liked(@RequestParam int vId,HttpServletRequest request){
        util.validateLogin(request);
        VideoComplexResponseDTO vDto = videoService.likeVideo(vId,util.getUserIdFromRequest(request));
        return ResponseEntity.status(201).body(vDto);
    }


    @PutMapping("/videos/dislike")
    public ResponseEntity<VideoComplexResponseDTO> dislike(@RequestParam int vId, HttpServletRequest request){
        util.validateLogin(request);
        VideoComplexResponseDTO vDto = videoService.dislikeVideo(vId,util.getUserIdFromRequest(request));
        return ResponseEntity.status(201).body(vDto);
    }

    @DeleteMapping("/videos/remove_reaction")
    public ResponseEntity<VideoComplexResponseDTO> removeReaction(@RequestParam int vId,HttpServletRequest request){
        util.validateLogin(request);
        VideoComplexResponseDTO vDto = videoService.removeReaction(vId,util.getUserIdFromRequest(request));
        return ResponseEntity.status(200).body(vDto);
    }

    @PostMapping("/videos/watch")
    public int  watch(@RequestParam int vId,HttpSession session, HttpServletRequest request) {
        util.validateLogin(request);
        return videoService.watch(vId, (int) session.getAttribute(USER_ID));
    }
    @PostMapping("/videos/upload_image")
    public String uploadProfileImage(@RequestParam int vId,@RequestParam (name = "image") MultipartFile file , HttpSession session, HttpServletRequest request){
        util.validateLogin(request);
        return videoService.uploadVideoImage(vId,file,util.getUserIdFromRequest(request));
    }
    @GetMapping("/videos")
    public List<VideoSimpleResponseDTO> searchByTitle(@RequestParam String title, HttpSession session){
        return videoService.searchByTitle(title,session);
    }


}
