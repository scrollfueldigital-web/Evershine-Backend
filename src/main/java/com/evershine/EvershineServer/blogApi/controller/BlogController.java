package com.evershine.EvershineServer.blogApi.controller;

import com.evershine.EvershineServer.blogApi.dto.BlogRequestDto;
import com.evershine.EvershineServer.blogApi.dto.BlogResponseDto;
import com.evershine.EvershineServer.blogApi.dto.PageResponseDto;
import com.evershine.EvershineServer.blogApi.dto.SearchResponse;
import com.evershine.EvershineServer.blogApi.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogResponseDto> createBlog(@Valid @RequestBody BlogRequestDto dto){
        return ResponseEntity.ok(blogService.createBlog(dto));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<BlogResponseDto>> getAllBlogs(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PageResponseDto<BlogResponseDto> pageResponses =
                blogService.getAllBlogs(pageNo, size, sortBy, sortDir);

        return ResponseEntity.ok(pageResponses);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BlogResponseDto> getBlogBySlug(@PathVariable String slug){
        return ResponseEntity.ok(blogService.getBlogBySlug(slug));
    }

    // GET /api/blogs/search?keyword=evershine
    @GetMapping("/search")
    public ResponseEntity<List<SearchResponse>> searchBlogs(@RequestParam String keyword) {
        return ResponseEntity.ok(blogService.getSearchResult(keyword));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogResponseDto> updateBlog(
            @PathVariable Long id,
            @RequestBody @Valid BlogRequestDto dto){
        return ResponseEntity.ok(blogService.updateBlog(id,dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        blogService.deleteBlog(id);
        return ResponseEntity.ok(Map.of("message", "Blog deleted successfully"));
    }
}
