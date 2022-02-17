package com.example.yoto.model.video;


import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;



@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserService userService;


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



    public Video uploadVideo(Video videoReq) {
        if (videoReq.getTitle() == null || videoReq.getTitle().isBlank()) {
            throw new BadRequestException("Title is mandatory");
        }
//        if (videoReq.getUploadDate() == null || videoReq.getUploadDate().isBlank()) {
//            throw new BadRequest
//        }
//        if(videoReq.getUserId()){
//            ..........
//        }
        if (videoReq.getVideoUrl() == null || videoReq.getVideoUrl().isBlank()) {
            throw new BadRequestException("No content to upload");
        }
        return videoRepository.save(videoReq);
    }


}
