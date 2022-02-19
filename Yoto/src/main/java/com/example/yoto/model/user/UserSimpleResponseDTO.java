package com.example.yoto.model.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSimpleResponseDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String aboutMe;
    private String profileImageUrl;
    private int followers;
    private int videos;
}