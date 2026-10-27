package com.evershine.EvershineServer.blogApi.repository;

import com.evershine.EvershineServer.blogApi.dto.SearchResponse;
import com.evershine.EvershineServer.blogApi.entity.Blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Long> {

    @Query("select b.id as id, b.title as title, b.slug as slug, b.imageUrl as imageUrl " +
       "from Blog b where LOWER(b.title) like LOWER(concat('%', :keyword, '%')) " +
       "order by b.title asc")
    List<SearchResponse> searchResponse(String keyword, Pageable page);


    Optional<Blog> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
