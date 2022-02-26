package com.example.yoto.model.video;

import com.example.yoto.model.user.UserSimpleResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoSimpleResponseDTO {
    private int id;
    private String title;
    private UserSimpleResponseDTO user;
    private LocalDateTime uploadDate;
    private String videoUrl;
    private int views;

}
