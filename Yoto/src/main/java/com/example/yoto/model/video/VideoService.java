package com.example.yoto.model.video;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.relationship.URTV.UserReactToVideo;
import com.example.yoto.model.relationship.URTV.UsersReactToVideosId;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserService;
import com.example.yoto.util.Util;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import static com.example.yoto.util.Util.*;


@Service
public class VideoService {

    @Autowired
    private Util util;

    public VideoComplexResponseDTO getById(int id) {
        Video video = util.videoGetById(id);
        return videoToComplexDTO(video);
    }

    public VideoSimpleResponseDTO uploadVideo(Video videoReq, int userId) {
        //TODO
        if (videoReq.getTitle().trim().isEmpty()){
            throw new BadRequestException("Title is mandatory");
        }
        if(videoReq.getTitle().length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException("Title is too long");
        }
        if (videoReq.getUploadDate().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Invalid date and time");
        }
        if (videoReq.getVideoUrl() == null || videoReq.getVideoUrl().isBlank()) {
            throw new BadRequestException("No content to upload");
        }
        videoReq.setUser(util.userGetById(userId));
        videoReq.setUploadDate(LocalDateTime.now());
        Video video = util.videoRepository.save(videoReq);
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
        User user =util.userGetById(userId);
        Video video = util.videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        UserReactToVideo userReactToVideo = new UserReactToVideo(usersReactToVideosId, user, video, c);
        util.userReactToVideoRepository.save(userReactToVideo);
        return video;
    }

    public VideoComplexResponseDTO removeReaction(int vId, int userId) {
        User user = util.userGetById(userId);
        Video video = util.videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        UserReactToVideo userReactToVideo = util.userReactToVideoRepository.findById(usersReactToVideosId)
                .orElseThrow(() -> new BadRequestException("You haven't reacted to this video yet"));
        util.userReactToVideoRepository.deleteById(usersReactToVideosId);
        return videoToComplexDTO(video);
    }

    public int watch(int vId, int userId) {
        User user = util.userGetById(userId);
        Video video = util.videoGetById(vId);
        video.getUsers().add(user);
        util.videoRepository.save(video);
        return video.getUsers().size();
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
        vDto.setLikes(util.userReactToVideoRepository.findAllByVideoIdAndReaction(video.getId(), '+').size());
        vDto.setDislikes(util.userReactToVideoRepository.findAllByVideoIdAndReaction(video.getId(), '-').size());
        return vDto;
    }

    @SneakyThrows
    public String uploadVideoImage(int vId, MultipartFile file, int user_id) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        Video video = util.videoGetById(vId);
        String videoTitle = video.getTitle();
        String fileName = videoTitle + "&" + System.nanoTime() + "." + fileExtension;
        Files.copy(file.getInputStream(), new File(UPLOAD_FILES_DIRECTORY + File.separator + fileName).toPath());
        video.setVideoUrl(fileName);
        util.videoRepository.save(video);
        return fileName;
    }

    public List<VideoSimpleResponseDTO> searchByTitle(String title, HttpSession session) {
        if (title == null && title.isEmpty()) {
            throw new BadRequestException("The submitted title is blank!");
        }
        List<VideoSimpleResponseDTO> videos = util.videoRepository
                .findAllByTitleContainsAndIsPrivate(title, false).stream()
                .map(VideoService::videoToSimpleDTO)
                .collect(Collectors.toList());
        Integer userId = (Integer) session.getAttribute(USER_ID);
        if (userId != null) {
            videos.addAll(util.videoRepository
                    .findAllByUserIdAndIsPrivate(userId, true).stream()
                    .map(VideoService::videoToSimpleDTO)
                    .collect(Collectors.toList()));
        }
        if (videos.isEmpty()) {
            throw new NotFoundException("Not matches videos with this title");
        }
        return videos;
    }

}
