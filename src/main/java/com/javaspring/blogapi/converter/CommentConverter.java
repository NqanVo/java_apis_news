package com.javaspring.blogapi.converter;

import com.javaspring.blogapi.dto.comment.CommentDTO;
import com.javaspring.blogapi.dto.comment.SubCommentDTO;
import com.javaspring.blogapi.model.CommentEntity;
import com.javaspring.blogapi.model.SubCommentEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommentConverter {
    public CommentEntity DTOToEntity(CommentDTO commentDTO) {
        CommentEntity commentEntity = new CommentEntity();
        BeanUtils.copyProperties(commentDTO, commentEntity);

        return commentEntity;
    }

    public CommentDTO EntityToDTO(CommentEntity commentEntity) {
        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(commentEntity, commentDTO);
        commentDTO.setExistsSubComment(commentEntity.getSubCommentEntityList().size() > 0);
        return commentDTO;
    }

    public CommentEntity Update_DTOToEntity(CommentDTO commentDTO, CommentEntity commentEntity) {
        String createBy = commentEntity.getCreatedBy();
        Date createDate = commentEntity.getCreatedDate();
        BeanUtils.copyProperties(commentDTO, commentEntity);
        commentEntity.setCreatedBy(createBy);
        commentEntity.setCreatedDate(createDate);
        return commentEntity;
    }

    // * ConverterSubComments
    public SubCommentEntity SubDTOToEntity(SubCommentDTO subCommentDTO) {
        SubCommentEntity entity = new SubCommentEntity();
        BeanUtils.copyProperties(subCommentDTO, entity);
        return entity;
    }

    public SubCommentDTO SubEntityToDTO(SubCommentEntity subCommentEntity) {
        SubCommentDTO dto = new SubCommentDTO();
        BeanUtils.copyProperties(subCommentEntity, dto);
        dto.setIdPrimaryComment(subCommentEntity.getCommentEntity().getId());
        return dto;
    }

    public SubCommentEntity Update_SubDTOToEntity(SubCommentDTO subCommentDTO, SubCommentEntity subCommentEntity) {
        String createBy = subCommentEntity.getCreatedBy();
        Date createDate = subCommentEntity.getCreatedDate();
        BeanUtils.copyProperties(subCommentDTO, subCommentEntity);
        subCommentEntity.setCreatedBy(createBy);
        subCommentEntity.setCreatedDate(createDate);
        return subCommentEntity;
    }
}
