package com.example.yoto.model.video;

import com.example.yoto.model.user.UserSimpleResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class VideoComplexResponseDTO {
    private int id;
    private String title;
    private UserSimpleResponseDTO user;
    private LocalDateTime uploadDate;
    private String videoUrl;
    private boolean isPrivate;
    private int views;
    private int likes;
    private int dislikes;
}
