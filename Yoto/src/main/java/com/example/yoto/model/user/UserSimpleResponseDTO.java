package com.example.yoto.model.user;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSimpleResponseDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String aboutMe;
    private String profileImageUrl;
    private int followers;
    private int videos;
}