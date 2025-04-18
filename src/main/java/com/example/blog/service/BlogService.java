package com.example.blog.service;

import com.example.blog.domain.Article;
import com.example.blog.dto.AddArticleRequest;
import com.example.blog.dto.ArticleResponseDto;
import com.example.blog.dto.UpdateArticleRequestDto;
import com.example.blog.repository.BlogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(Long id, UpdateArticleRequestDto requestDto) {
        Article article = blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found : " + id));

        article.update(requestDto.getTitle(), requestDto.getContent());

        return article;
    }
}
