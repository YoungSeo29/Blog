package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ArticleSummaryDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
