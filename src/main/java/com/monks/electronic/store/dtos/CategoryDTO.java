package com.monks.electronic.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private String categoryId;
    @Size(min = 4, message = "title must be atleast 4 chars !!")
    private String title;
    @NotBlank(message = "Description is required !!")
    private String description;
    @NotBlank
    private String coverImage;
}
