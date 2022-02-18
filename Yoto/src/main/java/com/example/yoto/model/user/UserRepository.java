package com.example.yoto.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
}
