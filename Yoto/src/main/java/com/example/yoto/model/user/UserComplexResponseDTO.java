package com.example.yoto.model.user;

import com.example.yoto.model.playList.PlayListComplexResponseDTO;
import com.example.yoto.model.video.VideoSimpleResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserComplexResponseDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String aboutMe;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private int followers;
    private Set<PlayListComplexResponseDTO> playlists;
    private Set<VideoSimpleResponseDTO> videos;
}
