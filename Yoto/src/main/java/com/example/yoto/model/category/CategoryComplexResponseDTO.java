package com.example.yoto.model.category;

import com.example.yoto.model.video.VideoSimpleResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CategoryComplexResponseDTO {
    private int id;
    private String title;
    private String categoryImageUrl;
    private String description;
    private LocalDate createDate;
    private int followers;
    private Set<VideoSimpleResponseDTO> videos;
}
