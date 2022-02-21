package com.example.yoto.controller;

import com.example.yoto.model.playList.PlayListComplexResponseDTO;
import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.playList.PlayListSimpleResponseDTO;
import com.example.yoto.model.playList.PlayListService;
import com.example.yoto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;
@RestController
public class PlayListController {

    @Autowired
    private PlayListService playListService;
    @Autowired
    private Util util;

    @GetMapping("/playlists/{id:[\\d]+}")
    public PlayListComplexResponseDTO getById(@PathVariable int id) {
        PlayListComplexResponseDTO playlistDto = playListService.getById(id);
        return playlistDto;
    }

    @PostMapping("/playlists/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PlayListSimpleResponseDTO createPlayList(@RequestBody Playlist playlist,HttpServletRequest request) {
       util.validateLogin(request);
        PlayListSimpleResponseDTO playListDTO = playListService.createPlaylist(playlist, util.getUserIdFromRequest(request));
        return playListDTO;
    }

    @DeleteMapping("/playlists/delete")
    public int deletePlaylist (@RequestParam int plId, HttpServletRequest request){
        util.validateLogin(request);
        return playListService.deletePlaylist( plId, util.getUserIdFromRequest(request));

    }
    @PostMapping("/playlists/add_video")
    public int addToPlaylist (@RequestParam int vId, @RequestParam int plId , HttpServletRequest request){
        util.validateLogin(request);
        int userId =  util.getUserIdFromRequest(request);
        return playListService.addVideo(vId,plId,userId);
    }

    @DeleteMapping("/playlists/delete_video")
    public int deleteFromPlaylist (@RequestParam int vId, @RequestParam int plid , HttpServletRequest request) {
        util.validateLogin(request);
        int userId =  util.getUserIdFromRequest(request);
        return playListService.deleteVideo(vId,plid,userId);
    }

    @PostMapping("/playlists/upload_background_image")
    public String uploadBackgroundImage(@RequestParam int plDto,@RequestParam (name = "background_image")MultipartFile file , HttpServletRequest request){
        util.validateLogin(request);
        return playListService.uploadBackgroundImage(plDto,file, util.getUserIdFromRequest(request));
    }
    @GetMapping("/playlists")
    public List<PlayListSimpleResponseDTO> searchByTitle(@RequestParam String title, HttpSession session){
        return playListService.searchByTitle(title,session);
    }

}
