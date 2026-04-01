package com.example.blog.dto;

import com.example.blog.domain.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ArticleViewResponseDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;
    private String authorNickname;

    public ArticleViewResponseDto(Article article, String nickname) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.author = article.getAuthor();
        this.authorNickname = nickname != null ? nickname : article.getAuthor();
    }
}
