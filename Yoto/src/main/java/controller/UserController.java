package controller;


import model.User;
import model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public User register(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }

    @GetMapping("/{id:[\\d]+}")
    public User getById(@PathVariable int id) {
        return userRepository.findById(id).orElseThrow();
    }

    @DeleteMapping
    public User delete(int id) {
        Optional<User> opt = userRepository.findById(id);
        if (!opt.isPresent()) {
            throw new NoSuchElementException("No user found with id " + id);
        }
        User user = opt.get();
        userRepository.deleteById(id);
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }


}
