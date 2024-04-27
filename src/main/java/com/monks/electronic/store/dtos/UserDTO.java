package com.monks.electronic.store.dtos;

import com.monks.electronic.store.util.ImageNameValid;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Builder
public class UserDTO {
    private String id;

    @Size(min = 3, max = 15, message = "Invalid name !!")
    private String name;

//    @Email(message = "Invalid email !!")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",message = "Invalid email !!")
    @NotBlank(message = "email can not be blank !!")
    private String email;

    @NotBlank(message = "password is required !!")
    private String password;

    @Size(min = 4, max = 6, message = "Invalid gender !!")
    private String gender;

    @NotBlank(message = "Write something about yourself !!")
    private String about;

    @ImageNameValid
    private String imageName;
}
