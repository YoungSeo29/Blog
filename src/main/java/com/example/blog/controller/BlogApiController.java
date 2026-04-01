package com.example.blog.controller;

import com.example.blog.domain.Article;
import com.example.blog.domain.User;
import com.example.blog.dto.AddArticleRequest;
import com.example.blog.dto.ArticleResponseDto;
import com.example.blog.dto.UpdateArticleRequestDto;
import com.example.blog.dto.user.MyInfoDto;
import com.example.blog.service.BlogService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;
    private final UserService userService;

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {

        Article savedArticle = blogService.save(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponseDto>> findAllArticles() {

        List<ArticleResponseDto> articles = blogService.findAll()
                .stream()
                .map(ArticleResponseDto::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);

    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponseDto> findArticle(@PathVariable("id") Long id) {

        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponseDto(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable("id") Long id) {

        blogService.delete(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable("id") Long id,
            @RequestBody UpdateArticleRequestDto request) {

        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedArticle);
    }

    // 내 정보
    @GetMapping("/api/user/me")
    public ResponseEntity<MyInfoDto> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        MyInfoDto dto = MyInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();

        System.out.println("email : " + email);
        System.out.println("nickname : " + user.getNickname());

        return ResponseEntity.ok(dto);
    }

//
//    // 프로필 사진 업로드
//    @PostMapping("/api/user/profile-image") {
//
//    }
//
//    // 내 글 목록 (페이지네이션)
//    @GetMapping("/api/articles?page=0&size=10") {
//
//    }
}
