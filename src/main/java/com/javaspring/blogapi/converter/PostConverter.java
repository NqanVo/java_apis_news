package com.javaspring.blogapi.converter;

import com.javaspring.blogapi.dto.post.PostRequestDTO;
import com.javaspring.blogapi.dto.post.PostResponseDTO;
import com.javaspring.blogapi.dto.category.CategoryResponseDTO;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.service.impl.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PostConverter {
    @Autowired
    private CategoryService categoryService;

    public PostEntity ConverterPostDTOToPost(PostRequestDTO postRequestDTO) {
        PostEntity postEntity = new PostEntity();
        BeanUtils.copyProperties(postRequestDTO, postEntity);
        return postEntity;
    }

    public PostResponseDTO ConverterPostToPostDTO(PostEntity postEntity) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();

        BeanUtils.copyProperties(postEntity, postResponseDTO);
        postResponseDTO.setCategory(new CategoryResponseDTO(postEntity.getCategoryEntity().getId(), postEntity.getCategoryEntity().getName(), postEntity.getCategoryEntity().getCode()));
        postResponseDTO.setTotalComments(postEntity.getCommentEntityList().stream().count());
        return postResponseDTO;
    }

    public PostEntity ConverterNewPostDTOToOldPost(PostRequestDTO postRequestDTO, PostEntity oldPostEntity) {
        String createBy = oldPostEntity.getCreatedBy();
        Date createDate = oldPostEntity.getCreatedDate();
        BeanUtils.copyProperties(postRequestDTO, oldPostEntity);
        oldPostEntity.setCreatedBy(createBy);
        oldPostEntity.setCreatedDate(createDate);
        return oldPostEntity;
    }
}
