package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.config.IsUserOrIsAdmin;
import com.javaspring.blogapi.converter.CommentConverter;
import com.javaspring.blogapi.dto.comment.CommentDTO;
import com.javaspring.blogapi.dto.comment.SubCommentDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.CommentEntity;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.model.SubCommentEntity;
import com.javaspring.blogapi.model.UserEntity;
import com.javaspring.blogapi.repository.CommentRepository;
import com.javaspring.blogapi.repository.PostRepository;
import com.javaspring.blogapi.repository.SubCommentRepository;
import com.javaspring.blogapi.service.CommentInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService implements CommentInterface {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubCommentRepository subCommentRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentConverter commentConverter;

    @Autowired
    private final IsUserOrIsAdmin userDetailsJwt = new IsUserOrIsAdmin();

    @Override
    public List<CommentDTO> findByIdPost(Long idPost, Pageable pageable) {
        List<CommentEntity> entityList = commentRepository.findByPostEntityId(idPost, pageable);
        if (entityList == null || entityList.isEmpty())
            throw new CustomException.NotFoundException("Không tìm thấy bình luận cho bài đăng: " + idPost);
        List<CommentDTO> dtoList = new ArrayList<>();
        for (CommentEntity cmt : entityList)
            dtoList.add(commentConverter.EntityToDTO(cmt));
        return dtoList;
    }

    @Override
    public void save(Long idPost, CommentDTO commentDTO) {
        PostEntity postEntity = postRepository.findById(idPost).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bài đăng: " + idPost));

        userDetailsJwt.getUserAndIsAdmin();
        UserEntity userEntity = userDetailsJwt.getUserEntity();

        CommentEntity commentEntity = new CommentEntity();
        commentDTO.setUserEntity(userEntity);
        commentDTO.setPostEntity(postEntity);

        if (commentDTO.getId() == null) {
            commentEntity = commentConverter.DTOToEntity(commentDTO);
        } else {
            commentEntity = commentRepository.findById(commentDTO.getId()).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bình luận"));
            if (!(userDetailsJwt.getUserEntity().equals(commentEntity.getUserEntity()) || userDetailsJwt.isAdmin()))
                throw new CustomException.UnauthorizedException("Không có quyền chỉnh sửa");
            commentEntity = commentConverter.Update_DTOToEntity(commentDTO, commentEntity);
        }
        commentEntity = commentRepository.save(commentEntity);
    }
    @Override
    public void deleteComment(Long idCmt) {
        userDetailsJwt.getUserAndIsAdmin();
        CommentEntity commentEntity = commentRepository.findById(idCmt).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bình luận"));
        if (!(userDetailsJwt.getUserEntity().equals(commentEntity.getUserEntity()) || userDetailsJwt.isAdmin()))
            throw new CustomException.UnauthorizedException("Không có quyền chỉnh sửa");
        commentRepository.deleteById(idCmt);
    }

    // * SUBCOMMENTS
    @Override
    public List<SubCommentDTO> subFindByIdComents(Long idCmt, Pageable pageable) {
        List<SubCommentEntity> subCommentEntities = subCommentRepository.findByCommentEntityId(idCmt, pageable);
        if(subCommentEntities.size() == 0) throw new CustomException.NotFoundException("Không có bình luận con của: "+ idCmt);
        List<SubCommentDTO> subCommentDTO = new ArrayList<>();
        for (SubCommentEntity entity : subCommentEntities)
            subCommentDTO.add(commentConverter.SubEntityToDTO(entity));
        return subCommentDTO;
    }

    @Override
    public void subSave(Long idCmt, SubCommentDTO subCommentDTO) {
        userDetailsJwt.getUserAndIsAdmin();
        CommentEntity commentEntity = commentRepository.findById(idCmt).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bình luận"));
        SubCommentEntity subCommentEntity = new SubCommentEntity();
        subCommentDTO.setCommentEntity(commentEntity);
        subCommentDTO.setUserEntity(commentEntity.getUserEntity());
        if(subCommentDTO.getId() == null){
            subCommentEntity = commentConverter.SubDTOToEntity(subCommentDTO);
        }else {
            subCommentEntity = subCommentRepository.findById(subCommentDTO.getId()).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bình luận"));
            if (!(userDetailsJwt.getUserEntity().equals(subCommentEntity.getUserEntity()) || userDetailsJwt.isAdmin()))
                throw new CustomException.UnauthorizedException("Không có quyền chỉnh sửa");
            subCommentEntity = commentConverter.Update_SubDTOToEntity(subCommentDTO, subCommentEntity);
        }
        subCommentEntity = subCommentRepository.save(subCommentEntity);
    }

    @Override
    public void deleteSubCmt(Long idSubCmt){
        userDetailsJwt.getUserAndIsAdmin();
        SubCommentEntity subCommentEntity = subCommentRepository.findById(idSubCmt).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy bình luận"));
        if (!(userDetailsJwt.getUserEntity().equals(subCommentEntity.getUserEntity()) || userDetailsJwt.isAdmin()))
            throw new CustomException.UnauthorizedException("Không có quyền chỉnh sửa");
        subCommentRepository.deleteById(idSubCmt);
    }


}
