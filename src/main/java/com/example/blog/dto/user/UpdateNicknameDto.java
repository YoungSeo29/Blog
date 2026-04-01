package com.example.blog.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateNicknameDto {
    private String nickname;
}
