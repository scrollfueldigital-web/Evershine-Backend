package com.evershine.EvershineServer.blogApi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class BlogResponseDto {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private List<String> keywords;
    private String imageUrl;
    private LocalDateTime createdAt;
}
