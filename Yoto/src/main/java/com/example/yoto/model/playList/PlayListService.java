package com.example.yoto.model.playList;



import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PlayListService {

    @Autowired
    private PlayListRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;


    public Playlist getById(int id) {
        return playlistGetBy(id);
    }


    public Playlist createPlaylist(Playlist playlist,int userId) {
        User user = userGetById(userId);
        if(playlist.getTitle() == null || playlist.getTitle().isBlank()){
           throw new BadRequestException("Title is mandatory");
       }
        if(user.getPlaylists().contains(playlist)){
            throw new BadRequestException("The playlist is in the user list");
        }
        playlist.setCreator(user);
        playlist.setCreateDate(LocalDateTime.now());
        playlist.setLastActualization(playlist.getCreateDate());
        return playlistRepository.save(playlist);
    }

    public int deletePlaylist(int plId, int userId) {
        User user = userGetById(userId);
        Playlist playlist = playlistGetBy(plId);
        if(!user.getPlaylists().contains(playlist)){
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

    public Playlist playlistGetBy(int id) {
        return playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Playlist not found"));
    }

    public User  userGetById(int id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Video videoGetById(int id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }


}
