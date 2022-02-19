package com.example.yoto.model.comment;


import com.example.yoto.model.user.UserSimpleResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentSimpleResponseDTO {
    private int id;
    private UserSimpleResponseDTO creator;
    private String text;
    private LocalDateTime creationDate;
    private int likes;
    private int dislikes;
    private int subComments;

}
