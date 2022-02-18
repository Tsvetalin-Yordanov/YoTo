package com.example.yoto.controller;

import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.playList.PlayListResponseDTO;
import com.example.yoto.model.playList.PlayListService;
import com.example.yoto.model.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.example.yoto.model.user.UserService.USER_ID;

@RestController
public class PlayListController {

    @Autowired
    private PlayListService playListService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/playlist/{id:[\\d]+}")
    public PlayListResponseDTO getById(@PathVariable int id) {
        Playlist playList = playListService.getById(id);
        PlayListResponseDTO playListDto = modelMapper.map(playList, PlayListResponseDTO.class);
        return playListDto;
    }

    @PostMapping("/playlist/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PlayListResponseDTO createPlayList(@RequestBody Playlist playlist, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        playListService.createPlaylist(playlist,(int)session.getAttribute("user_id"));
        PlayListResponseDTO playListDTO = modelMapper.map(playlist, PlayListResponseDTO.class);
        return playListDTO;
    }

    @DeleteMapping("/playlist/delete")
    public int deletePlaylist (@RequestParam int plId, HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        return playListService.deletePlaylist( plId,(int)session.getAttribute("user_id"));

    }
    @PostMapping("/playlist/add_video")
    public int addToPlaylist (@RequestParam int vId, @RequestParam int plId ,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session, request);
        int userId = (int) session.getAttribute(USER_ID);
        return playListService.addVideo(vId,plId,userId);
    }

    @DeleteMapping("/playlist/delete_video")
    public int deleteFromPlaylist (@RequestParam int vId, @RequestParam int plid ,HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int userId = (int) session.getAttribute(USER_ID);
        return playListService.deleteVideo(vId,plid,userId);
    }

}
