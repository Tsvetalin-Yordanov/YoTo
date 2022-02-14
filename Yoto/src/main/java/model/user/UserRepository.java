package model.user;

import model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    
    User findByEmail(String email);
   // User findByPhoneNumber(String phoneNumber);
    User findByEmailAndPassword(String email, String password);
}
