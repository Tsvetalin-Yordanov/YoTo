package com.example.yoto.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    List<User> findAllByFirstNameContains(String firstName);

    @Query("SELECT u FROM User u WHERE DAY (date_of_birth) = DAY(curdate()) AND MONTH(date_of_birth) = MONTH(curdate())")
    List<User>findAllUsersBirthDayToday();
}
