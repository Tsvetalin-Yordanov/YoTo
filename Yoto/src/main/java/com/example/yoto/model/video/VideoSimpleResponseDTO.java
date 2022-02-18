package com.example.yoto.model.video;

import com.example.yoto.model.category.Category;
import com.example.yoto.model.user.UserSimpleResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class VideoSimpleResponseDTO {
    private int id;
    private String title;
    private UserSimpleResponseDTO user;
    private LocalDateTime uploadDate;
    private String videoUrl;
    private int views;

}
