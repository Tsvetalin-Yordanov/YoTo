package com.example.yoto.model.playList;


import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import com.example.yoto.model.user.UserService;
import com.example.yoto.model.video.*;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.yoto.model.user.UserService.USER_ID;

@Service
public class PlayListService {

    @Autowired
    private PlayListRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;

    public PlayListComplexResponseDTO getById(int id) {
        Playlist playlist = playlistGetBy(id);
        return playlistToComplexDTO(playlist);
    }

    public PlayListSimpleResponseDTO createPlaylist(Playlist playlist, int userId) {
        User user = userGetById(userId);
        playlist.setCreator(user);
        if (playlist.getTitle() == null || playlist.getTitle().isBlank()) {
            throw new BadRequestException("Title is mandatory");
        }
        if (user.getPlaylists().contains(playlist)) {
            throw new BadRequestException("The playlist is in the user list");
        }
        playlist.setCreator(user);
        playlist.setCreateDate(LocalDateTime.now());
        playlist.setLastActualization(playlist.getCreateDate());
        playlist.setBackgroundUrl(playlist.getBackgroundUrl());
        playlist.setPrivate(playlist.isPrivate());
        playlistRepository.save(playlist);
        return playlistToSimpleDTO(playlist);
    }

    public int deletePlaylist(int plId, int userId) {
        User user = userGetById(userId);
        Playlist playlist = playlistGetBy(plId);
        if (!user.getPlaylists().contains(playlist)) {
            throw new BadRequestException("The playlist is not in the user list");
        }
        playlistRepository.delete(playlist);
        return user.getPlaylists().size();
    }

    public int addVideo(int vId, int plId, int userId) {
        User user = userGetById(userId);
        Playlist playlist = playlistGetBy(plId);
        Video video = videoGetById(vId);
        if (playlist.getVideos().contains(video)) {
            //TODO
            throw new BadRequestException("Video is in playlist");
        }
        playlist.setLastActualization(LocalDateTime.now());
        playlist.getVideos().add(video);
        playlistRepository.save(playlist);
        return playlist.getVideos().size();
    }

    public int deleteVideo(int vId, int plId, int userId) {
        User user = userGetById(userId);
        Playlist playlist = playlistGetBy(plId);
        Video video = videoGetById(vId);
        if (!playlist.getVideos().contains(video)) {
            throw new BadRequestException("Video is not in playlist");
        }
        playlist.setLastActualization(LocalDateTime.now());
        playlist.getVideos().remove(video);
        playlistRepository.save(playlist);
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
        plDto.setVideos(playlist.getVideos().isEmpty()
                ? new HashSet<>()
                : playlist.getVideos().stream().map(VideoService::videoToSimpleDTO)
                .collect(Collectors.toSet()));
        return plDto;
    }


    @SneakyThrows
    public String uploadBackgroundImage(int plId, MultipartFile file, int user_id) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        Playlist playlist = playlistGetBy(plId);
        String playlistTitle = playlist.getTitle();
        String fileName = playlistTitle + "&" + System.nanoTime() + "." + fileExtension;
        //TODO uploads set constant !
        Files.copy(file.getInputStream(), new File("uploads" + File.separator + fileName).toPath());
        playlist.setBackgroundUrl(fileName);
        playlistRepository.save(playlist);
        return fileName;
    }


    public List<PlayListSimpleResponseDTO> searchByTitle(String title, HttpSession session) {
        if (title == null && title.isEmpty()) {
            throw new BadRequestException("The submitted title is blank!");
        }
        List<PlayListSimpleResponseDTO> playlists = playlistRepository
                .findAllByTitleContainsAndIsPrivate(title, false).stream()
                .map(PlayListService::playlistToSimpleDTO)
                .collect(Collectors.toList());
        Integer userId = (Integer) session.getAttribute(USER_ID);
        if (userId != null) {
            playlists.addAll(playlistRepository
                    .findAllByCreatorIdAndIsPrivate(userId, true).stream()
                    .map(PlayListService::playlistToSimpleDTO)
                    .collect(Collectors.toList()));
        }
        if (playlists.isEmpty()) {
            throw new NotFoundException("Not matches playlists with this title");
        }
        return playlists;
    }

    //TODO move in Util
    public Playlist playlistGetBy(int id) {
        return playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Playlist not found"));
    }
    //TODO move in Util
    public User userGetById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
    //TODO move in Util
    public Video videoGetById(int id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }

}
