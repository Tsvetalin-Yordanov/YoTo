package com.example.yoto.model.playList;

import com.example.yoto.model.video.VideoSimpleResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PlayListComplexResponseDTO {
    private int id;
    private String title;
    private int creatorId;
    private LocalDateTime lastActualization;
    private boolean isPrivate;
    private String backgroundUrl;
    private Set<VideoSimpleResponseDTO> videos;
}
