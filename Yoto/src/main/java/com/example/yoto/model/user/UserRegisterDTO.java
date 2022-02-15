package com.example.yoto.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String aboutMe;
    private char gender;
    private String profileImageUrl;
    private String backgroundImageUrl;
}
