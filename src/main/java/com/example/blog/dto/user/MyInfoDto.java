package com.example.blog.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfoDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
}
