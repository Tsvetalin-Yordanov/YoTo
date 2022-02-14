package model.user;

import model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


<<<<<<< HEAD:Yoto/src/main/java/model/UserRepository.java
=======
    User findByEmail(String email);
   // User findByPhoneNumber(String phoneNumber);
    User findByEmailAndPassword(String email, String password);
>>>>>>> 691d112ce355261c35504be0625705714c5713b2:Yoto/src/main/java/model/user/UserRepository.java
}
