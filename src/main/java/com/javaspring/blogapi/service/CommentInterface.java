package com.javaspring.blogapi.service;

import com.javaspring.blogapi.dto.comment.CommentDTO;
import com.javaspring.blogapi.dto.comment.SubCommentDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentInterface {
    List<CommentDTO> findByIdPost(Long idPost, Pageable pageable);

    void save(Long idPost, CommentDTO commentDTO);

    void deleteComment(Long idCmt);

    // * SUBCOMMENTS
    List<SubCommentDTO> subFindByIdComents(Long idCmt, Pageable pageable);

    void subSave(Long idCmt, SubCommentDTO subCommentDTO);

    void deleteSubCmt(Long idSubCmt);
}
