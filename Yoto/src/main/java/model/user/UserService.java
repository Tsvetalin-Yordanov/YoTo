package model.user;

import model.exceptions.BadRequestException;
import model.exceptions.NotFoundException;
import model.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "logged_from";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


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
        if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\\\.[a-zA-Z0-9-]+)*$")) {
            throw new BadRequestException("Email is invalid");
        }
        //todo
//      if (dateOfBirth == null || dateOfBirth.isBlank()) {
//          throw new BadRequest
//      }
        if (userRepository.findByEmail(email) != null) {
            throw new BadRequestException("User already exists");
        }

//        if (userRepository.findByPhoneNumber(phoneNumber) != null) {
//            throw new BadRequestException("User already exists");
//        }
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
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user == null) {
             throw new UnauthorizedException("Wrong credentials");
        }
        return user;
    }

    public User getById(int id) {
        if (id > 0) {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                return optionalUser.get();
            }
            throw new NotFoundException("User not found");
        }

        throw new BadRequestException("Id is not positive");

    }

    public User DeleteById(int id) {
        if (id > 0) {
            User user = getById(id);
            userRepository.deleteById(id);
            return user;
        }
        throw new BadRequestException("Id is not positive");
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    //todo
    @Transactional
    public User edit(User user) {
        Optional<User> opt = userRepository.findById(user.getId());
        if (opt.isPresent()){
            return userRepository.save(user);
        }
        else{
            throw new NotFoundException("User not found");
        }
    }

    public void validateLogin(HttpSession session, HttpServletRequest request) {
        if (session.isNew() ||
                (!(Boolean) session.getAttribute(LOGGED)) ||
                (!request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM)))) {
            throw new UnauthorizedException("You have to log in!");
        }
    }
}