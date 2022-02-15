package controller;

import model.playList.PlayList;
import model.playList.PlayListResponseDTO;
import model.playList.PlayListService;
import model.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.geom.GeneralPath;

@RestController
public class PlayListController {

    @Autowired
    private PlayListService playListService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/play_lists/{d:[\\d]+}")
    public PlayListResponseDTO getById(@RequestBody int id) {
        PlayList playList = playListService.getById(id);
        PlayListResponseDTO playListDto = modelMapper.map(playList, PlayListResponseDTO.class);
        return playListDto;
    }

    @PostMapping("/play_list")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PlayListResponseDTO createPlayList(@RequestBody PlayList playList, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        playListService.createPlayList(playList);
        PlayListResponseDTO playListDTO = modelMapper.map(playList, PlayListResponseDTO.class);
        return playListDTO;
    }




}
