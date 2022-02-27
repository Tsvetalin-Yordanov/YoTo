package com.example.yoto.controller;

import com.example.yoto.model.video.*;
import com.example.yoto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.List;



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

    @GetMapping("/videos/order_by_upload_date")
    public List<VideoSimpleResponseDTO> getOrderByUploadDate(@RequestParam String validator,
                                                             @RequestParam(defaultValue = "0") int pageNumber,
                                                             @RequestParam(defaultValue = "26") int rowNumbers, HttpServletRequest request) {
        return videoService.getOrderVideosByUploadDate(validator, pageNumber, rowNumbers);
    }

    @GetMapping("/videos/order_by_watched")
    public List<VideoSimpleResponseDTO> getOrderByWatched(@RequestParam String validator,
                                                          @RequestParam(defaultValue = "0") int pageNumber,
                                                          @RequestParam(defaultValue = "26") int rowNumbers, HttpServletRequest request) {
        return videoService.getOrderVideosByWatchedCount(validator, pageNumber, rowNumbers);
    }

    @DeleteMapping("/videos")
    @ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Video is deleted!")
    public void deleteVideoById(@RequestParam int id, HttpServletRequest request) {
        videoService.deleteById(id);
    }

    @PutMapping("/videos/like")
    public ResponseEntity<VideoComplexResponseDTO> liked(@RequestParam int vId, HttpServletRequest request) {
        VideoComplexResponseDTO vDto = videoService.likeVideo(vId, util.getUserIdFromRequest(request));
        return ResponseEntity.status(201).body(vDto);
    }


    @PutMapping("/videos/dislike")
    public ResponseEntity<VideoComplexResponseDTO> dislike(@RequestParam int vId, HttpServletRequest request) {
        VideoComplexResponseDTO vDto = videoService.dislikeVideo(vId, util.getUserIdFromRequest(request));
        return ResponseEntity.status(201).body(vDto);
    }

    @DeleteMapping("/videos/remove_reaction")
    public ResponseEntity<VideoComplexResponseDTO> removeReaction(@RequestParam int vId, HttpServletRequest request) {
        VideoComplexResponseDTO vDto = videoService.removeReaction(vId, util.getUserIdFromRequest(request));
        return ResponseEntity.status(200).body(vDto);
    }

    @PostMapping("/videos/watch")
    public VideoComplexResponseDTO watch(@RequestParam int vId, HttpServletRequest request) {
        return videoService.watch(vId, util.getUserIdFromRequest(request));
    }

    @PostMapping("/videos/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<VideoComplexResponseDTO> uploadVideoImage(@RequestParam String title, @RequestParam boolean isPrivate, @RequestParam(name = "video") MultipartFile file, HttpServletRequest request) {
        VideoComplexResponseDTO video = videoService.uploadVideo(title,isPrivate, file, util.getUserIdFromRequest(request));
        return ResponseEntity.status(201).body(video);
    }

    @GetMapping("/videos/search_by_title")
    public List<VideoSimpleResponseDTO> searchByTitle(@RequestParam String title,
                                                      @RequestParam(defaultValue = "0") int pageNumber,
                                                      @RequestParam(defaultValue = "26") int rowNumbers, HttpServletRequest request) {
        return videoService.searchByTitle(title, request, pageNumber, rowNumbers);
    }

    @GetMapping("/videos/get_all")
    public List<VideoSimpleResponseDTO> getAllVideos(@RequestParam(defaultValue = "0") int pageNumber,
                                                     @RequestParam(defaultValue = "26") int rowNumbers, HttpServletRequest request) {
        return videoService.getAllVideos(pageNumber, rowNumbers, request);
    }

    @PutMapping("/videos/upload/dropbox")
    public ResponseEntity<VideoSimpleResponseDTO> uploadVideoToDropbox(@RequestParam int vId, HttpServletRequest request){
        VideoSimpleResponseDTO dto =  videoService.uploadVideoToDropbox(vId);
        return ResponseEntity.status(201).body(dto);
    }

}
