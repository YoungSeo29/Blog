package com.example.blog.controller;

import com.example.blog.domain.Article;
import com.example.blog.dto.ArticleListViewResponseDto;
import com.example.blog.dto.ArticleViewResponseDto;
import com.example.blog.service.BlogService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;
    private final UserService userService;

    @GetMapping("/articles")
    public String getArticles(Model model) {

        List<ArticleListViewResponseDto> articles = blogService.findAll().stream()
                .map(ArticleListViewResponseDto::new)
                .toList();

        model.addAttribute("articles", articles);

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable("id") Long id, Model model) {

        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponseDto(article));

        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {

        if( id == null ) {
            model.addAttribute("article", new ArticleViewResponseDto());
        } else {
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponseDto(article));
        }

        return "newArticle";
    }

    @GetMapping("/my-page")
    public String myPage(Principal principal, Model model) {
        System.out.println("view Controller - 마이페이지");

        String userName = principal.getName();
        System.out.println("유저 네임 : " + userName);

        return "myPage";
    }
}
