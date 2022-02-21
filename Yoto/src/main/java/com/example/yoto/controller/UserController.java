package com.example.yoto.controller;


import com.example.yoto.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<UserSimpleResponseDTO> register(@RequestBody UserRegisterDTO userDTO) {
        UserSimpleResponseDTO dto = userService.register(userDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/users/verify_registration/{id}")
    public UserSimpleResponseDTO verifyRegistration(@PathVariable int id){
        return userService.verifyRegistration(id);
    }

    @PostMapping("/users/log_in")
    public UserSimpleResponseDTO logIn(@RequestBody User user, HttpSession session, HttpServletRequest request) {
        UserSimpleResponseDTO dto = userService.login(user);
        session.setAttribute(UserService.USER_ID, dto.getId());
        session.setAttribute(UserService.LOGGED, true);
        session.setAttribute(UserService.LOGGED_FROM, request.getRemoteAddr());
        return dto;
    }

    @PutMapping("/users")
    public UserSimpleResponseDTO editUser(@RequestBody User user, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        UserSimpleResponseDTO userDTO = userService.edit(user);
        return userDTO;
    }

    //todo ask Krasi about session
    @DeleteMapping("/users")
    public UserSimpleResponseDTO delete(@RequestParam int id, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        UserSimpleResponseDTO dto = userService.DeleteById(id);
        return dto;
    }

    @GetMapping("/users/{id}")
    public UserComplexResponseDTO getById(@PathVariable int id) {
        UserComplexResponseDTO dto = userService.getById(id);
        return dto;
    }

    @GetMapping("/users")
    public List<UserSimpleResponseDTO> getAll() {
        List<UserSimpleResponseDTO> usersDTO = userService.getAll();
        return usersDTO;
    }

    @PostMapping("/users/logout")
    public void logOut(HttpSession session) {
        session.invalidate();
    }

    @PostMapping("/users/follow")
    public int followUser(@RequestParam int publisherId, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        return userService.followUser(publisherId, (int) session.getAttribute("user_id"));
    }

    @PostMapping("/users/unfollow")
    public int unFollowUser(@RequestParam int publisherId, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        return userService.unFollowUser(publisherId, (int) session.getAttribute("user_id"));
    }

    @PostMapping("users/upload_profile_image")
    public String uploadProfileImage(@RequestParam MultipartFile file , HttpSession session, HttpServletRequest request){
        userService.validateLogin(session, request);
        return userService.uploadProfileImage(file,(int) session.getAttribute("user_id"));
    }

    @PostMapping("users/upload_background_image")
    public String uploadBackgroundImage(@RequestParam MultipartFile file , HttpSession session, HttpServletRequest request){
        userService.validateLogin(session, request);
        return userService.uploadBackgroundImage(file,(int) session.getAttribute("user_id"));
    }

    @GetMapping("/users/search")
    public List<UserSimpleResponseDTO> searchByName(@RequestParam String name){
        return userService.searchByName(name);
    }


    @PutMapping("/users/reset_password")
    public UserSimpleResponseDTO resetPassword(@RequestBody UserChangePasswordDTO changePasswordDTO, HttpSession session, HttpServletRequest request){
        userService.validateLogin(session,request);
        return userService.resetPassword(changePasswordDTO,(int) session.getAttribute("user_id"));
    }

    @PutMapping("/users/forgotten_password")
    public UserSimpleResponseDTO forgottenPassword(@RequestParam String email){
        return userService.forgottenPassword(email);
    }
}

