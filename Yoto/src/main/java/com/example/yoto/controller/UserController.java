package com.example.yoto.controller;


import com.example.yoto.model.user.*;
import com.example.yoto.util.Util;
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
    @Autowired
    private Util util;

    @PostMapping("/users/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<UserSimpleResponseDTO> register(@RequestBody UserRegisterDTO userDTO) {
        UserSimpleResponseDTO dto = userService.register(userDTO);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/users/verify_registration/{encryptedId}")
    public UserSimpleResponseDTO verifyRegistration(@PathVariable String encryptedId){
        return userService.verifyRegistration(encryptedId);
    }

    @PostMapping("/users/log_in")
    public UserSimpleResponseDTO logIn(@RequestBody User user, HttpServletRequest request) {
        UserSimpleResponseDTO dto = userService.login(user);
        HttpSession session = request.getSession();
        session.setAttribute(util.USER_ID, dto.getId());
        session.setAttribute(util.LOGGED, true);
        session.setAttribute(util.LOGGED_FROM, request.getRemoteAddr());
        return dto;
    }

    @PutMapping("/users")
    public UserSimpleResponseDTO editUser(@RequestBody User user, HttpServletRequest request) {
        util.validateLogin(request);
        UserSimpleResponseDTO userDTO = userService.edit(user);
        return userDTO;
    }

    //todo ask Krasi about session
    @DeleteMapping("/users")
    public ResponseEntity<String> delete(@RequestParam int id, HttpServletRequest request) {
        util.validateLogin(request);
        userService.DeleteById(id);
        return ResponseEntity.status(204).body("User deleted successfully!");
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
    public int followUser(@RequestParam int publisherId, HttpServletRequest request) {
        util.validateLogin(request);
        return userService.followUser(publisherId, util.getUserIdFromRequest(request));
    }

    @PostMapping("/users/unfollow")
    public int unFollowUser(@RequestParam int publisherId, HttpServletRequest request) {
        util.validateLogin(request);
        return userService.unFollowUser(publisherId, util.getUserIdFromRequest(request));
    }

    @PostMapping("users/upload_profile_image")
    public String uploadProfileImage(@RequestParam MultipartFile file, HttpServletRequest request) {
        util.validateLogin(request);
        return userService.uploadProfileImage(file, util.getUserIdFromRequest(request));
    }

    @PostMapping("users/upload_background_image")
    public String uploadBackgroundImage(@RequestParam MultipartFile file, HttpServletRequest request) {
        util.validateLogin(request);
        return userService.uploadBackgroundImage(file, util.getUserIdFromRequest(request));
    }

    @GetMapping("/users/search")
    public List<UserSimpleResponseDTO> searchByName(@RequestParam String name) {
        return userService.searchByName(name);
    }


    @PutMapping("/users/reset_password")
    public UserSimpleResponseDTO resetPassword(@RequestBody UserChangePasswordDTO changePasswordDTO, HttpServletRequest request){
       util.validateLogin(request);
        return userService.resetPassword(changePasswordDTO,util.getUserIdFromRequest(request));
    }

    @PutMapping("/users/forgotten_password")
    public UserSimpleResponseDTO forgottenPassword(@RequestParam String email){
        return userService.forgottenPassword(email);
    }
}

