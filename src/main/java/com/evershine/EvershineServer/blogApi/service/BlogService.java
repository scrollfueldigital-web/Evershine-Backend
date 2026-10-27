package com.evershine.EvershineServer.blogApi.service;

import com.evershine.EvershineServer.blogApi.dto.BlogRequestDto;
import com.evershine.EvershineServer.blogApi.dto.BlogResponseDto;
import com.evershine.EvershineServer.blogApi.dto.PageResponseDto;
import com.evershine.EvershineServer.blogApi.dto.SearchResponse;
import com.evershine.EvershineServer.blogApi.entity.Blog;
import com.evershine.EvershineServer.blogApi.repository.BlogRepository;
import com.evershine.EvershineServer.projectExceptionHandler.BlogNotFoundException;
import com.evershine.EvershineServer.projectExceptionHandler.DuplicateFilterException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    @Transactional(readOnly = true)
    public BlogResponseDto getBlogBySlug(String slug) {
        Blog blog = blogRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new BlogNotFoundException("Blog not found with slug: " + slug
                        ));
        return toBlogResponseDto(blog);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<BlogResponseDto> getAllBlogs(int pageNo, int size, String sortBy, String sortDir) {

        Pageable pageable = verifyPageInfo(pageNo, size, sortBy, sortDir);

        Page<Blog> allBlogs = blogRepository.findAll(pageable);
        Page<BlogResponseDto> pageBlogResponse = allBlogs.map(this::toBlogResponseDto);
        return PageResponseDto.from(pageBlogResponse);
    }

    @Transactional
    public BlogResponseDto createBlog(BlogRequestDto blogRequestDto) {

        if(blogRepository.existsBySlug(blogRequestDto.getSlug()))
            throw new DuplicateFilterException("Blog already exist with slug: "+blogRequestDto.getSlug());

        Blog blog = toBlogFromRequest(blogRequestDto);

        blog = blogRepository.save(blog);
        return toBlogResponseDto(blog);
    }


    @Transactional
    public BlogResponseDto updateBlog(Long id, BlogRequestDto blogRequestDto) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + id));

        if(
                blogRequestDto.getSlug()!=null &&
                        !blogRequestDto.getSlug().equalsIgnoreCase(blog.getSlug())
                && blogRepository.existsBySlug(blogRequestDto.getSlug())
        ){
            throw new DuplicateFilterException("Blog already exist with slug: "+blogRequestDto.getSlug());
        }

        blog.setKeywords(blogRequestDto.getKeywords());
        blog.setContent(blogRequestDto.getContent());
        blog.setTitle(blogRequestDto.getTitle());
        blog.setImageUrl(blogRequestDto.getImageUrl());
        if(blogRequestDto.getSlug()!=null)
            blog.setSlug(blogRequestDto.getSlug());

        blogRepository.save(blog);

        return toBlogResponseDto(blog);
    }

    @Transactional(readOnly = true)
    public List<SearchResponse> getSearchResult(String keyword) {
        return blogRepository
                .searchResponse(keyword, PageRequest.of(0, 5));
    }

    @Transactional
    public String deleteBlog(Long id) {

        Blog blog = blogRepository.findById(id)
                .orElseThrow(() ->
                        new BlogNotFoundException("Blog not found with id: " + id)
                );

        blogRepository.delete(blog);
        return "Blog deletion successful with id: "+blog.getId();
    }

    private Pageable verifyPageInfo(int pageNo, int size, String sortBy, String sortDir) {

        pageNo = Math.max(0, pageNo);
        size = (size <= 0 || size > 8) ? 6 : size;

        Set<String> allowedSortFields =
                Set.of("createdAt", "title", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "title";
        }

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return PageRequest.of(
                pageNo,
                size,
                Sort.by(direction, sortBy)
        );
    }

    protected Blog toBlogFromRequest(BlogRequestDto blogRequestDto) {
        Blog blog = new Blog();
        blog.setContent(blogRequestDto.getContent());
        blog.setKeywords(blogRequestDto.getKeywords());
        blog.setSlug(blogRequestDto.getSlug());
        blog.setTitle(blogRequestDto.getTitle());
        blog.setImageUrl(blogRequestDto.getImageUrl());
        return blog;
    }

    protected BlogResponseDto toBlogResponseDto(Blog blog) {
        BlogResponseDto responseDto = new BlogResponseDto();
        responseDto.setId(blog.getId());
        responseDto.setTitle(blog.getTitle());
        responseDto.setSlug(blog.getSlug());
        responseDto.setImageUrl(blog.getImageUrl());
        responseDto.setContent(blog.getContent());
        responseDto.setKeywords(blog.getKeywords());
        responseDto.setCreatedAt(blog.getCreatedAt());
        return responseDto;
    }
}

