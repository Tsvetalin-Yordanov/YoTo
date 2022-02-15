package model.playList;


import model.exceptions.BadRequestException;
import model.exceptions.NotFoundException;
import model.video.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayListService {

    @Autowired
    private PlayListRepository playListRepository;



    public PlayList getById(int id) {
        if (id > 0) {
            Optional<PlayList> optionalPlayList = playListRepository.findById(id);
            if (optionalPlayList.isPresent()) {
                return optionalPlayList.get();
            }
            throw new NotFoundException("Play list not found");
        }
        throw new BadRequestException("Id is not positive");
    }


    public PlayList createPlayList(PlayList playList) {
       if(playList.getTitle() == null || playList.getTitle().isBlank()){
           throw new BadRequestException("Title is mandatory");
       }
         // TODO validate creator Id
        // TODO Validate create date
        // TODO Validate last actualization date
        return playListRepository.save(playList);
    }
}
