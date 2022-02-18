package com.example.yoto.model.user;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.exceptions.UnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "logged_from";
    public static final String USER_ID = "user_id";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;


    public User register(String firstName, String lastName, String password, String confirmPassword, String email, String phoneNumber, LocalDate dateOfBirth, String aboutMe, char gender, String profileImageUrl, String backgroundImageUrl) {
        if (firstName == null || firstName.isBlank()) {
            throw new BadRequestException("First name is mandatory");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new BadRequestException("Last name is mandatory");
        }
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password is mandatory");
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException("Password is too weak");
        }
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords mismatch");
        }
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is mandatory");
        }
        //todo check
        if (!email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new BadRequestException("Email is invalid");
        }
        //todo
        if (dateOfBirth == null) {
            throw new BadRequestException("Invalid password");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new BadRequestException("User already exists");
        }

        if (userRepository.findByPhoneNumber(phoneNumber) != null) {
            throw new BadRequestException("User already exists");
        }
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setDateOfBirth(dateOfBirth);
        user.setAboutMe(aboutMe);
        user.setGender(gender);
        user.setProfileImageUrl(profileImageUrl);
        user.setBackgroundImageUrl(backgroundImageUrl);
        userRepository.save(user);
        return user;
    }

    public User login(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Username is mandatory");
        }
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password is mandatory");
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UnauthorizedException("Wrong credentials");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Wrong credentials");
        }
        return user;
    }

    public User getById(int id) {
        if (id > 0) {
            return getUserById(id);
        }
        throw new BadRequestException("Id is not positive");
    }

    public User DeleteById(int id) {
        if (id > 0) {
            User user = getUserById(id);
            userRepository.deleteById(id);
            return user;
        }
        throw new BadRequestException("Id is not positive");
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    //todo
    public User edit(User user) {
        User user1 = getUserById(user.getId());
        return userRepository.save(user);
    }
    public List<UserResponseDTO> followUser(int publisherId, HttpSession session, HttpServletRequest request) {
        validateLogin(session, request);
        //Todo msg exeption

        User observer = getUserById((int) session.getAttribute("user_id"));
        User publisher = getUserById(publisherId);
        if (publisher.getObserverUsers().contains(observer)) {

            throw new BadRequestException("Already exists in the list of followers");
        }
        publisher.getObserverUsers().add(observer);
        userRepository.save(publisher);
        return publisher.getObserverUsers().stream().map(user -> modelMapper.map(user, UserResponseDTO.class)).collect(Collectors.toList());
    }

    public List<UserResponseDTO> unFollowUser(int publisherId, HttpSession session, HttpServletRequest request) {
        validateLogin(session, request);
        //Todo msg exeption
        User observer = getUserById((int) session.getAttribute("user_id"));
        User publisher = getUserById(publisherId);
        if (!publisher.getObserverUsers().contains(observer)) {
            throw new BadRequestException("Observer not follow this user");
        }
        publisher.getObserverUsers().remove(observer);
        userRepository.save(publisher);
        return publisher.getObserverUsers().stream().map(user -> modelMapper.map(user, UserResponseDTO.class)).collect(Collectors.toList());
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
}
