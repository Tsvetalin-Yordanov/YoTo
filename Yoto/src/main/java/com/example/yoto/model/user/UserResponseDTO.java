package com.example.yoto.model.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String aboutMe;
    private char gender;
    private String profileImageUrl;
    private String backgroundImageUrl;
}
