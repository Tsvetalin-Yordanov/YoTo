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
        PlayListSimpleResponseDTO playListDTO = playListService.createPlaylist(playlist, util.getUserIdFromRequest(request));
        return playListDTO;
    }

    @DeleteMapping("/playlists/delete")
    public int deletePlaylist (@RequestParam int plId, HttpServletRequest request){
        return playListService.deletePlaylist( plId, util.getUserIdFromRequest(request));

    }
    @PostMapping("/playlists/add_video")
    public int addToPlaylist (@RequestParam int vId, @RequestParam int plId , HttpServletRequest request){
        int userId =  util.getUserIdFromRequest(request);
        return playListService.addVideo(vId,plId,userId);
    }

    @DeleteMapping("/playlists/delete_video")
    public int deleteFromPlaylist (@RequestParam int vId, @RequestParam int plid , HttpServletRequest request) {
        int userId =  util.getUserIdFromRequest(request);
        return playListService.deleteVideo(vId,plid,userId);
    }

    @PostMapping("/playlists/upload_background_image")
    public String uploadBackgroundImage(@RequestParam int plId,@RequestParam (name = "background_image")MultipartFile file , HttpServletRequest request){
        return playListService.uploadBackgroundImage(plId,file);
    }
    @GetMapping("/playlists/search_by_title")
    public List<PlayListSimpleResponseDTO> searchByTitle(@RequestParam String title,HttpServletRequest request){
        return playListService.searchByTitle(title,request);
    }

    @GetMapping("/playlists/get_all")
    public List<PlayListSimpleResponseDTO> getAllPlaylists(HttpServletRequest request){
        return playListService.getAllPlaylists(request);
    }

}
