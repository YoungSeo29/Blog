package com.example.blog.service;

import com.example.blog.domain.Article;
import com.example.blog.domain.User;
import com.example.blog.dto.AddArticleRequest;
import com.example.blog.dto.UpdateArticleRequestDto;
import com.example.blog.repository.BlogRepository;
import com.example.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    // 글 새로 작성 시 캐시 무효화
    @CacheEvict( value = "articleCount", allEntries = true)
    public Article save(AddArticleRequest request, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));

        return blogRepository.save(request.toEntity(username, user.getId()));
    }

    // 글 삭제 시 캐시 무효화
    @CacheEvict( value = "articleCount", allEntries = true)
    public void delete(Long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.deleteById(id);
    }

    @Cacheable("articleCount")
    public long getArticleCount() {
        return blogRepository.count();
    }

    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("not found : " + id));
    }


    public Page<Article> findAll(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    public Slice<Article> findAllSlice(Pageable pageable) {
        return blogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Article> findByUserId(Long userId, Pageable pageable) {
        return blogRepository.findByUserId(userId, pageable);
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
