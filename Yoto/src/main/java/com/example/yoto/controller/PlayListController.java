package com.example.yoto.controller;

import com.example.yoto.model.playList.PlayListComplexResponseDTO;
import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.playList.PlayListSimpleResponseDTO;
import com.example.yoto.model.playList.PlayListService;
import com.example.yoto.model.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

import static com.example.yoto.model.user.UserService.USER_ID;

@RestController
public class PlayListController {

    @Autowired
    private PlayListService playListService;
    @Autowired
    private UserService userService;


    @GetMapping("/playlists/{id:[\\d]+}")
    public PlayListComplexResponseDTO getById(@PathVariable int id) {
        PlayListComplexResponseDTO playlistDto = playListService.getById(id);
        return playlistDto;
    }

    @PostMapping("/playlists/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PlayListSimpleResponseDTO createPlayList(@RequestBody Playlist playlist, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        PlayListSimpleResponseDTO playListDTO = playListService.createPlaylist(playlist,(int)session.getAttribute("user_id"));
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

    @PostMapping("/playlists/upload_background_image")
    public String uploadBackgroundImage(@RequestParam int plDto,@RequestParam (name = "background_image")MultipartFile file , HttpSession session, HttpServletRequest request){
        userService.validateLogin(session, request);
        return playListService.uploadBackgroundImage(plDto,file,(int) session.getAttribute("user_id"));
    }
    @GetMapping("/playlists")
    public List<PlayListSimpleResponseDTO> searchByTitle(@RequestParam String title, HttpSession session, HttpServletRequest request){
        return playListService.searchByTitle(title,session);
    }

}
