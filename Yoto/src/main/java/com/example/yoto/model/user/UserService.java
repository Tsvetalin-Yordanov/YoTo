package com.example.yoto.model.user;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.exceptions.UnauthorizedException;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoService;
import com.example.yoto.model.video.VideoSimpleResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "logged_from";
    public static final String USER_ID = "user_id";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserSimpleResponseDTO register(UserRegisterDTO userDTO) {
        if (userDTO.getFirstName() == null || userDTO.getFirstName() .isBlank()) {
            throw new BadRequestException("First name is mandatory");
        }
        if (userDTO.getLastName() == null || userDTO.getLastName() .isBlank()) {
            throw new BadRequestException("Last name is mandatory");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new BadRequestException("Password is mandatory");
        }
        if (!userDTO.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException("Password is too weak");
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new BadRequestException("Passwords mismatch");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail() .isBlank()) {
            throw new BadRequestException("Email is mandatory");
        }
        //todo check
        if (!userDTO.getEmail() .matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new BadRequestException("Email is invalid");
        }
        //todo
        if (userDTO.getDateOfBirth() == null) {
            throw new BadRequestException("Invalid date of birth");
        }
        if (userRepository.findByEmail(userDTO.getEmail() ) != null) {
            throw new BadRequestException("User already exists");
        }

        if (userRepository.findByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new BadRequestException("User already exists");
        }
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setAboutMe(userDTO.getAboutMe());
        user.setGender(userDTO.getGender());
        user.setProfileImageUrl(userDTO.getProfileImageUrl());
        user.setBackgroundImageUrl(userDTO.getBackgroundImageUrl());
        userRepository.save(user);
        return userToSimpleDTO(user);
    }

    public UserSimpleResponseDTO login(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadRequestException("Username is mandatory");
        }
        if (user.getPassword() == null || user.getPassword() .isBlank()) {
            throw new BadRequestException("Password is mandatory");
        }
        User user1 = userRepository.findByEmail(user.getEmail());
        if (user1 == null) {
            throw new UnauthorizedException("Wrong email");
        }
        if (!passwordEncoder.matches(user.getPassword(), user1.getPassword())) {
            throw new UnauthorizedException("Wrong credentials");
        }
        return userToSimpleDTO(user1);
    }

    public UserComplexResponseDTO getById(int id) {
        if (id > 0) {

            return userToComplexDTO(getUserById(id));
        }
        throw new BadRequestException("Id is not positive");
    }

    public UserSimpleResponseDTO DeleteById(int id) {
        if (id > 0) {
            User user = getUserById(id);
            userRepository.deleteById(id);
            return userToSimpleDTO(user);
        }
        throw new BadRequestException("Id is not positive");
    }

    public List<UserSimpleResponseDTO> getAll() {
        List<UserSimpleResponseDTO> dtos = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User user: users) {
            dtos.add(userToSimpleDTO(user));
        }
        return dtos;
    }

    //todo
    public UserSimpleResponseDTO edit(User user) {
        getUserById(user.getId());
        return userToSimpleDTO(userRepository.save(user));
    }

    public int followUser(int publisherId,int observerId) {

        //Todo msg exeption

        User observer = getUserById(observerId);
        User publisher = getUserById(publisherId);
        if (publisher.getObserverUsers().contains(observer)) {

            throw new BadRequestException("Already exists in the list of followers");
        }
        publisher.getObserverUsers().add(observer);
        userRepository.save(publisher);
        return publisher.getObserverUsers().size();
    }

    public int unFollowUser(int publisherId,int observerId) {
        //Todo msg exeption
        User observer = getUserById(observerId);
        User publisher = getUserById(publisherId);
        if (!publisher.getObserverUsers().contains(observer)) {
            throw new BadRequestException("Observer not follow this user");
        }
        publisher.getObserverUsers().remove(observer);
        userRepository.save(publisher);
        return publisher.getObserverUsers().size();
    }


    public void validateLogin(HttpSession session, HttpServletRequest request) {
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean sameIp = request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM));
        if (newSession || !logged || !sameIp) {
            throw new UnauthorizedException("You have to log in!");
        }
    }

    private User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public static UserSimpleResponseDTO userToSimpleDTO(User user) {
        UserSimpleResponseDTO userDto = new UserSimpleResponseDTO();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAboutMe(user.getAboutMe());
        userDto.setProfileImageUrl(user.getProfileImageUrl());
        userDto.setFollowers(user.getObserverUsers().size());
        userDto.setVideos(user.getVideos()!=null?user.getVideos().size():0);
        return userDto;
    }

    private UserComplexResponseDTO userToComplexDTO(User user){
        Set<VideoSimpleResponseDTO> videos = new HashSet<>();
        for (Video video : user.getVideos()) {
            videos.add(VideoService.videoToSimpleDTO(video));
        }

        UserComplexResponseDTO dto = new UserComplexResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAboutMe(user.getAboutMe());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setBackgroundImageUrl(user.getBackgroundImageUrl());
        dto.setFollowers(user.getObserverUsers().size());
        dto.setVideos(videos);
        return dto;
    }
}
