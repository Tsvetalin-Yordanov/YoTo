package com.example.yoto.controller;


import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRegisterDTO;
import com.example.yoto.model.user.UserResponseDTO;
import com.example.yoto.model.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/users/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterDTO userDTO) {
        String firstName = userDTO.getFirstName();
        String lastName = userDTO.getLastName();
        String password = userDTO.getPassword();
        String confirmPassword = userDTO.getConfirmPassword();
        String email = userDTO.getEmail();
        String phoneNumber = userDTO.getPhoneNumber();
        LocalDate dateOfBirth = userDTO.getDateOfBirth();
        String aboutMe = userDTO.getAboutMe();
        char gender = userDTO.getGender();
        String profilePictureURL = userDTO.getProfileImageUrl();
        String backgroundPictureURL = userDTO.getBackgroundImageUrl();
        User user = userService.register(firstName, lastName, password, confirmPassword, email, phoneNumber, dateOfBirth, aboutMe, gender, profilePictureURL, backgroundPictureURL);
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return ResponseEntity.status(201).body(dto);
    }

    @PostMapping("/users/log_in")
    public UserResponseDTO logIn(@RequestBody User user, HttpSession session, HttpServletRequest request) {
        String email = user.getEmail();
        String password = user.getPassword();
        User u = userService.login(email,password);
        session.setAttribute(UserService.USER_ID, u.getId());
        session.setAttribute(UserService.LOGGED, true);
        session.setAttribute(UserService.LOGGED_FROM, request.getRemoteAddr());
        UserResponseDTO dto = modelMapper.map(u, UserResponseDTO.class);
        return dto;
    }

    @PutMapping("/users")
    public UserResponseDTO editUser(@RequestBody User user, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        User u = userService.edit(user);
        UserResponseDTO userDTO = modelMapper.map(u, UserResponseDTO.class);
        return userDTO;
    }

    //todo ask Krasi about session
    @DeleteMapping("/users")
    public UserResponseDTO delete(@RequestParam int id, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        User user = userService.DeleteById(id);
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return dto;
    }

    @GetMapping("/users/{id}")
    public UserResponseDTO getById(@PathVariable int id) {
        User user = userService.getById(id);
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return dto;
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAll() {
        List<User> users = userService.getAll();
        List<UserResponseDTO> usersDTO = users.stream().map(u -> modelMapper.map(u, UserResponseDTO.class)).collect(Collectors.toList());
        return usersDTO;
    }

    @PostMapping("/user/logout")
    public void logOut(HttpSession session) {
        session.invalidate();
    }

    @PostMapping("/users/follow")
    public List<UserResponseDTO> followUser(@RequestParam int observer, HttpSession session, HttpServletRequest request) {

        return userService.followUser(observer, session, request);
    }
    @PostMapping("/users/unfollow")
    public List<UserResponseDTO> unFollowUser(@RequestParam int observer, HttpSession session, HttpServletRequest request) {
        return userService.unFollowUser(observer, session, request);
    }

//    @PutMapping("/users/reset_password")
//    public UserResponseDTO resetPassword(){
//
//    }
}
