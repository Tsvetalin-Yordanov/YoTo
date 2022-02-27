package com.example.yoto.model.playList;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.user.User;
import com.example.yoto.model.video.*;
import com.example.yoto.util.Util;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.yoto.util.Util.TITLE_MAX_LENGTH;
import static com.example.yoto.util.Util.UPLOAD_FILES_DIRECTORY;
import static com.example.yoto.util.Util.USER_ID;

@Service
public class PlayListService {

    @Autowired
    private Util util;


    public PlayListComplexResponseDTO getById(int id) {
        Playlist playlist = util.playlistGetById(id);
        return playlistToComplexDTO(playlist);
    }

    public PlayListSimpleResponseDTO createPlaylist(Playlist playlist, int userId) {
        User user = util.userGetById(userId);
        playlist.setCreator(user);
        String title = playlist.getTitle();
        if (title.trim().isEmpty() || title.length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException("Invalid playlist title");
        }
        if (user.getPlaylists().contains(playlist)) {
            throw new BadRequestException("The playlist is in the user list");
        }
        playlist.setCreator(user);
        playlist.setCreateDate(LocalDateTime.now());
        playlist.setLastActualization(playlist.getCreateDate());
        playlist.setBackgroundUrl(playlist.getBackgroundUrl());
        playlist.setPrivate(playlist.isPrivate());
        util.playlistRepository.save(playlist);
        return playlistToSimpleDTO(playlist);
    }

    public int deletePlaylist(int plId, int userId) {
        User user = util.userGetById(userId);
        Playlist playlist = util.playlistGetById(plId);
        if (!user.getPlaylists().contains(playlist)) {
            throw new BadRequestException("The playlist is not in the user list");
        }
        util.playlistRepository.delete(playlist);
        return user.getPlaylists().size();
    }

    public int addVideo(int vId, int plId, int userId) {
        util.userGetById(userId);
        Playlist playlist = util.playlistGetById(plId);
        Video video = util.videoGetById(vId);
        if (playlist.getVideos().contains(video)) {
            throw new BadRequestException("Video is in playlist");
        }
        playlist.setLastActualization(LocalDateTime.now());
        playlist.getVideos().add(video);
        util.playlistRepository.save(playlist);
        return playlist.getVideos().size();
    }

    public int deleteVideo(int vId, int plId, int userId) {
        util.userGetById(userId);
        Playlist playlist = util.playlistGetById(plId);
        Video video = util.videoGetById(vId);
        if (!playlist.getVideos().contains(video)) {
            throw new BadRequestException("Video is not in playlist");
        }
        playlist.setLastActualization(LocalDateTime.now());
        playlist.getVideos().remove(video);
        util.playlistRepository.save(playlist);
        return playlist.getVideos().size();
    }

    private static PlayListSimpleResponseDTO playlistToSimpleDTO(Playlist playlist) {
        PlayListSimpleResponseDTO plDto = new PlayListSimpleResponseDTO();
        plDto.setId(playlist.getId());
        plDto.setTitle(playlist.getTitle());
        plDto.setCreatorId(playlist.getCreator().getId());
        plDto.setLastActualization(playlist.getLastActualization());
        plDto.setPrivate(playlist.isPrivate());
        Optional<Video> video = playlist.getVideos().stream().findFirst();
        plDto.setFirstVideoUrl(video.isPresent() ? video.get().getVideoUrl() : "Play list is empty!");
        return plDto;
    }

    public PlayListComplexResponseDTO playlistToComplexDTO(Playlist playlist) {
        PlayListComplexResponseDTO plDto = new PlayListComplexResponseDTO();
        plDto.setId(playlist.getId());
        plDto.setTitle(playlist.getTitle());
        plDto.setCreatorId(playlist.getCreator().getId());
        plDto.setLastActualization(playlist.getLastActualization());
        plDto.setBackgroundUrl(playlist.getBackgroundUrl());
        plDto.setPrivate(playlist.isPrivate());
        plDto.setVideos(playlist.getVideos().isEmpty() ? new HashSet<>() : playlist.getVideos().stream().map(VideoService::videoToSimpleDTO).collect(Collectors.toSet()));
        return plDto;
    }


    @SneakyThrows
    public String uploadBackgroundImage(int plId, MultipartFile file) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (!(contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg"))) {
            throw new BadRequestException("Invalid image type");
        }
        Playlist playlist = util.playlistGetById(plId);
        String playlistTitle = playlist.getTitle();
        String fileName = playlistTitle + "&" + System.nanoTime() + "." + fileExtension;
        Files.copy(file.getInputStream(), new File(UPLOAD_FILES_DIRECTORY + File.separator + fileName).toPath());
        playlist.setBackgroundUrl(fileName);
        util.playlistRepository.save(playlist);
        return fileName;
    }


    public List<PlayListSimpleResponseDTO> searchByTitle(String title, HttpServletRequest request, int pageNumber, int rowNumbers) {
        if (title.trim().isEmpty() || title.length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException("The submitted title is blank!");
        }
        List<Playlist> playlists;
        if (pageNumber >= 0 && rowNumbers > 0) {
            Pageable page = PageRequest.of(pageNumber, rowNumbers);
            playlists = util.playlistRepository.findAllByTitleContainsAndIsPrivate(title, false, page);

            Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
            if (userId != null) {
                playlists.addAll(util.playlistRepository.findAllByCreatorIdAndIsPrivate(userId, true, page));
            }
            if (playlists.isEmpty()) {
                throw new NotFoundException("Not matches playlists with this title");
            }
        } else {
            throw new BadRequestException("Invalid parameters");
        }
        return playlists.stream()
                .map(PlayListService::playlistToSimpleDTO)
                .collect(Collectors.toList());
    }

    public List<PlayListSimpleResponseDTO> getAllPlaylists(HttpServletRequest request, int pageNumber, int rowNumbers) {
        List<Playlist> playlists;
        if (pageNumber >= 0 && rowNumbers > 0) {
            Pageable pages = PageRequest.of(pageNumber, rowNumbers);
            playlists = util.playlistRepository.findAllByIsPrivate(false, pages);
            Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
            if (userId != null) {
                int limitSize = pages.getPageSize() - playlists.size();
                Pageable pageForPrivate = PageRequest.of(0, limitSize);
                playlists.addAll(util.playlistRepository.findAllByCreatorIdAndIsPrivate(userId, true, pageForPrivate));
            }
            if (playlists.isEmpty()) {
                throw new NotFoundException("Playlists are not found");
            }
        } else {
            throw new BadRequestException("Invalid parameters");
        }
        return playlists.stream()
                .map(PlayListService::playlistToSimpleDTO)
                .collect(Collectors.toList());
    }

}
