package com.javaspring.blogapi.repository;

import com.javaspring.blogapi.model.CommentEntity;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.model.SubCommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostEntityId(Long idPost, Pageable pageable);
}
