package com.javaspring.blogapi.repository;

import com.javaspring.blogapi.model.CommentEntity;
import com.javaspring.blogapi.model.SubCommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubCommentEntity, Long> {
//    List<SubCommentEntity> findByCommentEntity(CommentEntity commentEntity);
    List<SubCommentEntity> findByCommentEntityId(Long idCmt, Pageable pageable);

}
