package com.example.yoto.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserChangePasswordDTO {
    private String oldPass;
    private String newPass;
    private String confirmPass;
}
