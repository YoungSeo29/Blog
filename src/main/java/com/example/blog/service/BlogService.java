package com.example.blog.service;

import com.example.blog.domain.Article;
import com.example.blog.dto.AddArticleRequest;
import com.example.blog.dto.ArticleResponseDto;
import com.example.blog.dto.UpdateArticleRequestDto;
import com.example.blog.repository.BlogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String username) {
        return blogRepository.save(request.toEntity(username));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(Long id) {
        Article article = blogRepository.findById(id)
                        .orElseThrow( () -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(Long id, UpdateArticleRequestDto requestDto) {
        Article article = blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(requestDto.getTitle(), requestDto.getContent());

        return article;
    }

    // 게시글 작성한 유저인지 확인하는 메서드
    private static void authorizeArticleAuthor(Article article) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!article.getAuthor().equals(username)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
