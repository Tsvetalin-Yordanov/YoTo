package com.example.yoto.model.video;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;
import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.relationship.userReactToVideo.UserReactToVideo;
import com.example.yoto.model.relationship.userReactToVideo.UsersReactToVideosId;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserService;
import com.example.yoto.util.Util;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.yoto.util.Util.*;


@Service
public class VideoService {

    @Autowired
    private Util util;

    private static int applyAsInt(Video v) {
        return v.getUsers().size();
    }

    public VideoComplexResponseDTO getById(int id) {
        Video video = util.videoGetById(id);
        return videoToComplexDTO(video);
    }

    public VideoSimpleResponseDTO uploadVideo(Video videoReq, int userId) {
        //TODO
        if (videoReq.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title is mandatory");
        }
        if (videoReq.getTitle().length() > TITLE_MAX_LENGTH) {
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

    public void deleteById(int id) {
        util.videoGetById(id);
        util.userRepository.deleteById(id);

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
        User user = util.userGetById(userId);
        Video video = util.videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        UserReactToVideo userReactToVideo = new UserReactToVideo(usersReactToVideosId, user, video, c);
        util.userReactToVideoRepository.save(userReactToVideo);
        return video;
    }

    public VideoComplexResponseDTO removeReaction(int vId, int userId) {
        util.userGetById(userId);
        Video video = util.videoGetById(vId);
        UsersReactToVideosId usersReactToVideosId = new UsersReactToVideosId(userId, vId);
        util.userReactToVideoRepository.findById(usersReactToVideosId)
                .orElseThrow(() -> new BadRequestException("You haven't reacted to this video yet"));
        util.userReactToVideoRepository.deleteById(usersReactToVideosId);
        return videoToComplexDTO(video);
    }

    public VideoComplexResponseDTO watch(int vId, int userId) {
        User user = util.userGetById(userId);
        Video video = util.videoGetById(vId);
        video.getUsers().add(user);
        util.videoRepository.save(video);
        return videoToComplexDTO(video);
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

    public List<VideoSimpleResponseDTO> searchByTitle(String title, HttpServletRequest request,int pageNumber,int rowNumbers) {
        if (title == null && title.isEmpty()) {
            throw new BadRequestException("The submitted title is blank!");
        }
        Pageable page = PageRequest.of(pageNumber, rowNumbers);
        List<VideoSimpleResponseDTO> videos = util.videoRepository
                .findAllByTitleContainsAndIsPrivate(title, false,page).stream()
                .map(VideoService::videoToSimpleDTO)
                .collect(Collectors.toList());
        Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
        if (userId != null) {
            videos.addAll(util.videoRepository
                    .findAllByUserIdAndIsPrivate(userId, true,page).stream()
                    .map(VideoService::videoToSimpleDTO)
                    .collect(Collectors.toList()));
        }
        if (videos.isEmpty()) {
            throw new NotFoundException("Not matches videos with this title");
        }
        return videos;
    }

    public List<VideoSimpleResponseDTO> getOrderVideosByUploadDate(String validator,int pageNumber,int rowNumbers) {
        Pageable pages = PageRequest.of(pageNumber, rowNumbers);
        List<VideoSimpleResponseDTO> videos = new ArrayList<>();
        if (validator.equals("desc")) {
            videos = util.videoRepository
                    .findAllByOrderByUploadDateDesc(pages).stream()
                    .map(VideoService::videoToSimpleDTO)
                    .collect(Collectors.toList());
        } else if (validator.equals("asc")) {
            videos = util.videoRepository
                    .findAllByOrderByUploadDateAsc(pages).stream()
                    .map(VideoService::videoToSimpleDTO)
                    .collect(Collectors.toList());
        } else {
            throw new BadRequestException("Invalid parameters");
        }
        if (videos.isEmpty()) {
            throw new NotFoundException("Not matches videos with this title");
        }
        return videos;
    }


    public List<VideoSimpleResponseDTO> getOrderVideosByWatchedCount(String validator,int pageNumber,int rowNumbers) {
        Pageable pages = PageRequest.of(pageNumber, rowNumbers);
        List<VideoSimpleResponseDTO> videos = new ArrayList<>();
        if (validator.equals("asc")) {
            videos = util.videoRepository
                    .findAll(pages).stream()
                    .sorted(Comparator.comparingInt(v -> v.getUsers().size()))
                    .map(VideoService::videoToSimpleDTO)
                    .collect(Collectors.toList());
        } else if (validator.equals("desc")) {
            videos = util.videoRepository
                    .findAll(pages).stream()
                    .sorted((video1, video2) -> Integer.compare(video2.getUsers().size(), video1.getUsers().size()))
                    .map(VideoService::videoToSimpleDTO)
                    .collect(Collectors.toList());
        } else {
            throw new BadRequestException("Invalid parameters");
        }
        if (videos.isEmpty()) {
            throw new NotFoundException("Not matches videos with this title");
        }
        return videos;
    }

    public List<VideoSimpleResponseDTO> getAllVideos(int pageNumber, int rowNumbers, HttpServletRequest request) {
        Pageable pages = PageRequest.of(pageNumber, rowNumbers);
        List<VideoSimpleResponseDTO> videos = util.videoRepository
                .findAllByIsPrivate(false, pages).stream()
                .map(VideoService::videoToSimpleDTO)
                .collect(Collectors.toList());
        Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
        if (userId != null && videos.size() < pages.getPageSize()) {
            int limitSize = pages.getPageSize() - videos.size();
            videos.addAll(util.videoRepository
                    .findAllByUserIdAndIsPrivate(userId, true,pages).stream()
                    .limit(limitSize)
                    .map(VideoService::videoToSimpleDTO)
                    .collect(Collectors.toList()));
        }
        if (videos.isEmpty()) {
            throw new NotFoundException("Videos are not found");
        }
        return videos;
    }

    @SneakyThrows
    public VideoSimpleResponseDTO uploadVideoToDropbox(int vId) {

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config,util.ACCESS_TOKEN);

        String filename = util.videoGetById(vId).getVideoUrl();

        try (InputStream in = new FileInputStream("uploads" + File.separator + filename)) {
            FileMetadata metadata = client.files().uploadBuilder("/"+filename)
                    .uploadAndFinish(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

        return null;
    }
}
