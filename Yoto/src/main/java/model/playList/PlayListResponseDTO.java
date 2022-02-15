package model.playList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class PlayListResponseDTO {

    private String title;
    private int creatorId;
    private LocalDateTime createDate;
    private LocalDateTime lastActualization;
    private boolean isPrivate;


}
