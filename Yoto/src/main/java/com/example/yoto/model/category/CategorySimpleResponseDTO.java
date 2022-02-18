package com.example.yoto.model.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategorySimpleResponseDTO {
    private int id;
    private String title;
    private String categoryImageUrl;
}
