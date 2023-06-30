package com.javaspring.blogapi.repository;

import com.javaspring.blogapi.model.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Boolean existsByTitle(String title);
}
