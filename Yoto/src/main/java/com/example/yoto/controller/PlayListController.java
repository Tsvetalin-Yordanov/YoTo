package com.example.yoto.controller;

import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.playList.PlayListSimpleResponseDTO;
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


    @GetMapping("/playlists/{id:[\\d]+}")
    public PlayListSimpleResponseDTO getById(@PathVariable int id) {
        Playlist playList = playListService.getById(id);
        PlayListSimpleResponseDTO playListDto = modelMapper.map(playList, PlayListSimpleResponseDTO.class);
        return playListDto;
    }

    @PostMapping("/playlists/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PlayListSimpleResponseDTO createPlayList(@RequestBody Playlist playlist, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        playListService.createPlaylist(playlist,(int)session.getAttribute("user_id"));
        PlayListSimpleResponseDTO playListDTO = modelMapper.map(playlist, PlayListSimpleResponseDTO.class);
        return playListDTO;
    }

    @DeleteMapping("/playlists/delete")
    public int deletePlaylist (@RequestParam int plId, HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        return playListService.deletePlaylist( plId,(int)session.getAttribute("user_id"));

    }
    @PostMapping("/playlists/add_video")
    public int addToPlaylist (@RequestParam int vId, @RequestParam int plId ,HttpSession session, HttpServletRequest request){
        userService.validateLogin(session, request);
        int userId = (int) session.getAttribute(USER_ID);
        return playListService.addVideo(vId,plId,userId);
    }

    @DeleteMapping("/playlists/delete_video")
    public int deleteFromPlaylist (@RequestParam int vId, @RequestParam int plid ,HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        int userId = (int) session.getAttribute(USER_ID);
        return playListService.deleteVideo(vId,plid,userId);
    }

}
