package com.example.yoto.model.playList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class PlayListSimpleResponseDTO {

    private String title;
    private int creatorId;
    private LocalDateTime lastActualization;
    private boolean isPrivate;
    private String firstVideoUrl;
}
