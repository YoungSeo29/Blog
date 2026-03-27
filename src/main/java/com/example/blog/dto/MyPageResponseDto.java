package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MyPageResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private List<ArticleSummaryDto> articles;

}
