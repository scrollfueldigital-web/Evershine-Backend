package com.evershine.EvershineServer.blogApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BlogRequestDto {
    @NotBlank(message = "Title should not be empty!")
    @Size(max = 300, message = "Title cannot exceed 300 characters")
    private String title;

    @NotEmpty(message = "Image URL required!")
    private String imageUrl;
    
    @NotBlank(message = "Slug required!")
    @Size(max = 100, message = "Slug should not exceed 100 characters")
    private String slug;

    @NotBlank(message = "Content is required")
    private String content;

    private List<String> keywords;
}
