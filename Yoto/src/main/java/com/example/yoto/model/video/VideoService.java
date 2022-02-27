package com.example.yoto.model.video;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
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
    @Autowired
    VideoDAO videoDAO;

    public VideoComplexResponseDTO getById(int id) {
        Video video = util.videoGetById(id);
        return videoToComplexDTO(video);
    }

    @SneakyThrows
    public VideoComplexResponseDTO uploadVideo(String title, boolean isPrivate, MultipartFile file, int creatorId) {
        if (title.trim().isEmpty()) {
            throw new BadRequestException("Title is mandatory");
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException("Title is too long");
        }
        String contentType = file.getContentType();
        if (!contentType.equals("video/mp4")) {
            throw new BadRequestException("Ivalid video type");
        }
        Video video = new Video();
        video.setTitle(title);
        video.setPrivate(isPrivate);
        video.setUser(util.userGetById(creatorId));
        video.setUploadDate(LocalDateTime.now());
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = title + "&" + System.nanoTime() + "." + fileExtension;
        Files.copy(file.getInputStream(), new File(UPLOAD_FILES_DIRECTORY + File.separator + fileName).toPath());
        video.setVideoUrl(fileName);
        util.videoRepository.save(video);
        return videoToComplexDTO(video);
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


    public List<VideoSimpleResponseDTO> searchByTitle(String title, HttpServletRequest request, int pageNumber, int rowNumbers) {
        if (title == null && title.isEmpty()) {
            throw new BadRequestException("The submitted title is blank!");
        }
        List<Video> videos;
        if (pageNumber >= 0 && rowNumbers > 0) {
            Pageable page = PageRequest.of(pageNumber, rowNumbers);
            videos = util.videoRepository.findAllByTitleContainsAndIsPrivate(title, false, page);
            Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
            if (userId != null) {
                videos.addAll(util.videoRepository.findAllByUserIdAndIsPrivate(userId, true, page));
            }
            if (videos.isEmpty()) {
                throw new NotFoundException("Not matches videos with this title");
            }
        } else {
            throw new BadRequestException("Invalid parameters");
        }
        return videos.stream()
                .map(VideoService::videoToSimpleDTO)
                .collect(Collectors.toList());
    }

    public List<VideoSimpleResponseDTO> getOrderVideosByUploadDate(String orderBY, int pageNumber, int rowNumbers) {
        List<Video> videos;
        if (pageNumber >= 0 && rowNumbers > 0) {
            Pageable pages = PageRequest.of(pageNumber, rowNumbers);
            if (orderBY.equals("desc")) {
                videos = util.videoRepository.findAllByOrderByUploadDateDesc(pages);
            } else if (orderBY.equals("asc")) {
                videos = util.videoRepository.findAllByOrderByUploadDateAsc(pages);
            } else {
                throw new BadRequestException("Invalid parameters");
            }
            if (videos.isEmpty()) {
                throw new NotFoundException("Not matches videos with this title");
            }
        } else {
            throw new BadRequestException("Invalid parameters");
        }
        return videos.stream()
                .map(VideoService::videoToSimpleDTO)
                .collect(Collectors.toList());
    }

    //@SneakyThrows
    public List<VideoSimpleResponseDTO> getOrderVideosByWatchedCount(String orderBY, int pageNumber, int rowNumbers) {
        if ((orderBY.equalsIgnoreCase("asc") || orderBY.equalsIgnoreCase("desc")) && pageNumber >= 0 && rowNumbers > 0) {
            try {
                return videoDAO.getOrderVideosByWatchedCount(orderBY, pageNumber, rowNumbers);
            } catch (SQLException e) {
                throw new NotFoundException("Not have videos");
            }
        } else {
            throw new BadRequestException("Invalid parameters");
        }
    }

    public List<VideoSimpleResponseDTO> getAllVideos(int pageNumber, int rowNumbers, HttpServletRequest request) {
        List<Video> videos;
        if (pageNumber >= 0 && rowNumbers > 0) {
            Pageable pages = PageRequest.of(pageNumber, rowNumbers);
            videos = util.videoRepository.findAllByIsPrivate(false, pages);
            Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
            if (userId != null && videos.size() < pages.getPageSize()) {
                int limitSize = pages.getPageSize() - videos.size();
                Pageable pageForPrivate = PageRequest.of(0, limitSize);
                videos.addAll(util.videoRepository.findAllByUserIdAndIsPrivate(userId, true, pageForPrivate));
            }
            if (videos.isEmpty()) {
                throw new NotFoundException("Videos are not found");
            }
        } else {
            throw new BadRequestException("Invalid parameters");
        }
        return videos.stream()
                .map(VideoService::videoToSimpleDTO)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public VideoSimpleResponseDTO uploadVideoToDropbox(int vId) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config,ACCESS_TOKEN);

        Video video = util.videoGetById(vId);
        String filename = video.getVideoUrl();

        try (InputStream in = new FileInputStream("uploads" + File.separator + filename)) {
            FileMetadata metadata = client.files().uploadBuilder("/" + filename)
                    .uploadAndFinish(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

        return videoToSimpleDTO(video);
    }
}
