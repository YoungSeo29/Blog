package com.example.blog.controller;

import com.example.blog.dto.ArticleListViewResponseDto;
import com.example.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {

        List<ArticleListViewResponseDto> articles = blogService.findAll().stream()
                .map(ArticleListViewResponseDto::new)
                .toList();

        model.addAttribute("articles", articles);

        return "articleList";
    }
}
