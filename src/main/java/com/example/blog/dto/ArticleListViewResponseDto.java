package com.example.blog.dto;

import com.example.blog.domain.Article;
import lombok.Getter;

@Getter
public class ArticleListViewResponseDto {

    private final Long id;
    private final String title;
    private String summary;

    public ArticleListViewResponseDto(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        String plainText = article.getContent()
                .replaceAll("<[^>]*>", "")
                .replaceAll("data:image[^;]*;base64,[^\"]*", "[이미지]");

        this.summary = plainText.substring(0, Math.min(100, plainText.length())) + "...";
    }
}
