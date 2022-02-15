package controller;

import model.user.User;
import model.user.UserRegisterDTO;
import model.user.UserResponseDTO;
import model.user.UserService;
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
        User u = userService.login(email, password);
        session.setAttribute(UserService.LOGGED, true);
        session.setAttribute(UserService.LOGGED_FROM, request.getRemoteAddr());
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return dto;
    }

    @GetMapping("/users/{id:[\\d]+}")
    public UserResponseDTO getById(@PathVariable int id) {
        User user = userService.getById(id);
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return dto;
    }

    @PutMapping
    public UserResponseDTO editUser(@RequestBody User user, HttpSession session,HttpServletRequest request){
        userService.validateLogin(session, request);
        User u = userService.edit(user);
        UserResponseDTO userDTO = modelMapper.map(u,UserResponseDTO.class);
        return userDTO;
    }

    //todo ask Krasi about session
    @DeleteMapping("/users")
    public UserResponseDTO delete(int id, HttpSession session, HttpServletRequest request) {
        userService.validateLogin(session, request);
        User user = userService.DeleteById(id);
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        return dto;
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAll() {
        List<User> users = userService.getAll();
        List<UserResponseDTO> usersDTO = users.stream().map(u -> modelMapper.map(u, UserResponseDTO.class)).collect(Collectors.toList());
        return usersDTO;
    }


}
