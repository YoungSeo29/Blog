package com.example.blog.controller;

import com.example.blog.domain.Article;
import com.example.blog.domain.User;
import com.example.blog.dto.ArticleListViewResponseDto;
import com.example.blog.dto.ArticleViewResponseDto;
import com.example.blog.service.BlogService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;
    private final UserService userService;

    @GetMapping("/articles")
    public String getArticles(Model model, @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Article> articlePage = blogService.findAll(pageable);

        List<ArticleListViewResponseDto> articles = articlePage.getContent()
                .stream()
                .map(ArticleListViewResponseDto::new)
                .toList();

        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable("id") Long id, Model model) {

        Article article = blogService.findById(id);
        User user = userService.findByEmail(article.getAuthor());
        String nickname = user.getNickname();

        model.addAttribute("article", new ArticleViewResponseDto(article, nickname));

        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {

        if( id == null ) {
            model.addAttribute("article", new ArticleViewResponseDto());
        } else {
            Article article = blogService.findById(id);
            User user = userService.findByEmail(article.getAuthor());

            model.addAttribute("article", new ArticleViewResponseDto(article, user.getNickname()));
        }

        return "newArticle";
    }

    @GetMapping("/my-page")
    public String myPage(Principal principal, Model model) {

        System.out.println("viewcontroller");


        String userName = principal.getName();
        System.out.println(userName);

        User user = userService.findByEmail(userName);
        model.addAttribute("user", user);
        // 아래는 임시
        model.addAttribute("articles", List.of());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);


        return "myPage";

    }

    @GetMapping("/coupon")
    public String coupon() {
        return "coupon";
    }
}
