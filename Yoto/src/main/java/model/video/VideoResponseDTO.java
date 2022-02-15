package model.video;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class VideoResponseDTO {

    private int id;
    private String title;
    private int userId;
    private LocalDateTime uploadDate;
    private String videoUrl;
    private boolean isPrivate;


}
