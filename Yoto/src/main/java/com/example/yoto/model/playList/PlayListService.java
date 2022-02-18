package com.example.yoto.model.playList;



import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayListService {

    @Autowired
    private PlayListRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;


    public Playlist getById(int id) {
        if (id > 0) {
            Optional<Playlist> optionalPlayList = playlistRepository.findById(id);
            if (optionalPlayList.isPresent()) {
                return optionalPlayList.get();
            }
            throw new NotFoundException("Playlist not found");
        }
        throw new BadRequestException("Id is not positive");
    }


    public Playlist createPlaylist(Playlist playlist,int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if(playlist.getTitle() == null || playlist.getTitle().isBlank()){
           throw new BadRequestException("Title is mandatory");
       }
        if(user.getPlaylists().contains(playlist)){
            throw new BadRequestException("The playlist is in the user list");
        }
        user.getPlaylists().add(playlist);
        playlistRepository.save(playlist);
        return playlistRepository.save(playlist);
    }

    public int deletePlaylist(int plId, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Playlist playlist = playlistRepository.findById(plId).orElseThrow(()-> new NotFoundException("Play list not found"));
        if(!user.getPlaylists().contains(playlist)){
            throw new BadRequestException("The playlist is not in the user list");
        }
        playlistRepository.delete(playlist);
        return user.getPlaylists().size();
    }
}
