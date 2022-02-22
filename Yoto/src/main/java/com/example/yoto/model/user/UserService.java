package com.example.yoto.model.user;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.exceptions.UnauthorizedException;
import com.example.yoto.model.playList.PlayListComplexResponseDTO;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoService;
import com.example.yoto.model.video.VideoSimpleResponseDTO;
import com.example.yoto.util.Util;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.yoto.util.Util.*;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private Util util;


    public UserSimpleResponseDTO register(UserRegisterDTO userDTO) {
        String firstName = userDTO.getFirstName();
        if (firstName.trim().isEmpty() || firstName.length() > USER_NAME_MAX_LENGTH) {
            throw new BadRequestException("Invalid first name!");
        }
        String lastName = userDTO.getLastName();
        if (lastName.trim().isEmpty() || lastName.length() > USER_NAME_MAX_LENGTH) {
            throw new BadRequestException("Invalid last name!");
        }
        String password = userDTO.getPassword();
        if (password.trim().isEmpty() || password.length() > USER_PASSWORD_MAX_LENGTH) {
            throw new BadRequestException("Invalid password!");
        }
        if (!userDTO.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException("Password is too weak!");
        }
        String configPassword = userDTO.getConfirmPassword();
        if (configPassword.trim().isEmpty() || configPassword.length() > USER_PASSWORD_MAX_LENGTH) {
            throw new BadRequestException("Invalid config password!");
        }
        if (!userDTO.getPassword().equals(configPassword)) {
            throw new BadRequestException("Passwords mismatch!");
        }
        String email = userDTO.getEmail();
        if (email.trim().isEmpty() || email.length() > USER_EMAIL_MAX_LENGTH) {
            throw new BadRequestException("Invalid email!");
        }
        if (!email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new BadRequestException("Invalid email!");
        }
        if (userDTO.getDateOfBirth() == null) {
            throw new BadRequestException("Invalid date of birth!");
        }
        String phoneNumber = userDTO.getPhoneNumber();
        if (phoneNumber.trim().isEmpty() || phoneNumber.length() > USER_PHONE_NUMBER_MAX_LENGTH) {
            throw new BadRequestException("Invalid phoneNumber!");
        }
        String aboutMe = userDTO.getAboutMe();
        if (aboutMe != null && aboutMe.length() > USER_ABOUT_ME_MAX_LENGTH) {
            throw new BadRequestException("The text is too long!");
        }
        if (util.userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("User already exists!");
        }

        if (util.userRepository.findByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new BadRequestException("User already exists!");
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
        util.userRepository.save(user);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("kaltodor11@gmail.com");
        msg.setTo(user.getEmail());
        msg.setSubject("Verify account");
        msg.setText("You have to verify tour account.\nPlease follow this link: http://localhost:3333/users/verify_registration/" + user.getId());
        javaMailSender.send(msg);

        return userToSimpleDTO(user);
    }

    public UserSimpleResponseDTO verifyRegistration(int id) {
        User user = util.userGetById(id);
        user.setVerified(true);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("kaltodor11@gmail.com");
        msg.setTo(user.getEmail());
        msg.setSubject("Verified");
        msg.setText("You have verified your account");
        javaMailSender.send(msg);
        util.userRepository.save(user);

        return userToSimpleDTO(user);
    }

    public UserSimpleResponseDTO login(User user) {
        String email = user.getEmail();
        if (email.trim().isEmpty() || email.length() > USER_EMAIL_MAX_LENGTH) {
            throw new BadRequestException("Invalid email!");
        }
        String password = user.getPassword();
        if (password.trim().isEmpty() || password.length() > USER_PASSWORD_MAX_LENGTH) {
            throw new BadRequestException("Invalid password!");
        }
        User user1 = util.userRepository.findByEmail(user.getEmail());
        if (user1 == null) {
            throw new NotFoundException("Wrong credentials!");
        }
        if (!user1.isVerified()) {
            throw new BadRequestException("You have to verify your account first!");
        }
        if (!passwordEncoder.matches(user.getPassword(), user1.getPassword())) {
            throw new UnauthorizedException("Wrong credentials!");
        }

        return userToSimpleDTO(user1);
    }

    public UserComplexResponseDTO getById(int id) {
        if (id > 0) {
            return userToComplexDTO(util.userGetById(id));
        }
        throw new BadRequestException("Id is not positive");
    }

    public boolean deleteById(int id) {
        if (id > 0) {
            util.userGetById(id);
            util.userRepository.deleteById(id);
            return true;
        } else {
            throw new BadRequestException("Id is not positive");
        }
    }

    public List<UserSimpleResponseDTO> getAll() {
        List<UserSimpleResponseDTO> dtos = new ArrayList<>();
        List<User> users = util.userRepository.findAll();
        for (User user : users) {
            dtos.add(userToSimpleDTO(user));
        }
        return dtos;
    }

    public UserSimpleResponseDTO edit(User user) {
        util.userGetById(user.getId());
        return userToSimpleDTO(util.userRepository.save(user));
    }

    public int followUser(int publisherId, int observerId) {
        User observer = util.userGetById(observerId);
        User publisher = util.userGetById(publisherId);
        if (publisher.getObserverUsers().contains(observer)) {
            throw new BadRequestException("Already exists in the list of followers");
        }
        publisher.getObserverUsers().add(observer);
        util.userRepository.save(publisher);
        return publisher.getObserverUsers().size();
    }

    public int unFollowUser(int publisherId, int observerId) {
        User observer = util.userGetById(observerId);
        User publisher = util.userGetById(publisherId);
        if (!publisher.getObserverUsers().contains(observer)) {
            throw new BadRequestException("Observer not follow this user");
        }
        publisher.getObserverUsers().remove(observer);
        util.userRepository.save(publisher);
        return publisher.getObserverUsers().size();
    }

    public static UserSimpleResponseDTO userToSimpleDTO(User user) {
        UserSimpleResponseDTO userDto = new UserSimpleResponseDTO();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAboutMe(user.getAboutMe());
        userDto.setProfileImageUrl(user.getProfileImageUrl());
        userDto.setFollowers(user.getObserverUsers().size());
        userDto.setVideos(!user.getVideos().isEmpty() ? 0 : user.getVideos().size());
        return userDto;
    }

    private UserComplexResponseDTO userToComplexDTO(User user) {
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
        dto.setPlaylists(user.getPlaylists().stream().map(playlist -> modelMapper.map(playlist, PlayListComplexResponseDTO.class)).collect(Collectors.toSet()));
        dto.setVideos(videos);
        return dto;
    }

    @SneakyThrows
    public String uploadProfileImage(MultipartFile file, int user_id) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        User user = util.userGetById(user_id);
        String userName = user.getFirstName();
        String fileName = userName + "&" + System.nanoTime() + "." + fileExtension;
        Files.copy(file.getInputStream(), new File(UPLOAD_FILES_DIRECTORY + File.separator + fileName).toPath());
        user.setProfileImageUrl(fileName);
        util.userRepository.save(user);
        return fileName;
    }

    @SneakyThrows
    public String uploadBackgroundImage(MultipartFile file, int user_id) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        User user = util.userGetById(user_id);
        String userName = user.getLastName();
        String fileName = userName + "&" + System.nanoTime() + "." + fileExtension;
        Files.copy(file.getInputStream(), new File(UPLOAD_FILES_DIRECTORY + File.separator + fileName).toPath());
        user.setBackgroundImageUrl(fileName);
        util.userRepository.save(user);
        return fileName;
    }

    public List<UserSimpleResponseDTO> searchByName(String name) {
        if (name.trim().isEmpty()) {
            throw new BadRequestException("Name is mandatory!");
        }
        List<UserSimpleResponseDTO> dtos = new LinkedList<>();
        List<User> users = util.userRepository.findAllByFirstNameContains(name);
        for (User user : users) {
            dtos.add(userToSimpleDTO(user));
        }
        return dtos;
    }

    public UserSimpleResponseDTO resetPassword(UserChangePasswordDTO changePasswordDTO, int user_id) {
        User user = util.userGetById(user_id);
        String oldPass = changePasswordDTO.getOldPass();
        String newPass = changePasswordDTO.getNewPass();
        String confirmPass = changePasswordDTO.getConfirmPass();

        if (oldPass.trim().isEmpty() || oldPass.length() > USER_PASSWORD_MAX_LENGTH) {
            throw new BadRequestException("Old password is mandatory");
        }
        if (newPass.trim().isEmpty() || newPass.length() > USER_PASSWORD_MAX_LENGTH) {
            throw new BadRequestException("New password is mandatory");
        }
        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new BadRequestException("Wrong old password");
        }
        if (!newPass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException("New password is too weak");
        }
        if (!newPass.equals(confirmPass)) {
            throw new BadRequestException("Passwords mismatch");
        }
        if (newPass.equals(oldPass)) {
            throw new BadRequestException("New password can not be the old password");
        }
        user.setPassword(passwordEncoder.encode(newPass));
        util.userRepository.save(user);
        return userToSimpleDTO(user);
    }

    public UserSimpleResponseDTO forgottenPassword(String email) {
        User user = util.userRepository.findByEmail(email);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("kaltodor11@gmail.com");
        msg.setTo(email);
        msg.setSubject("Forgotten password");
        msg.setText("Your new password is <1234>\nPlease change your password to a stronger one!");
        javaMailSender.send(msg);

        user.setPassword(passwordEncoder.encode("1234"));
        util.userRepository.save(user);

        return userToSimpleDTO(user);
    }
}
