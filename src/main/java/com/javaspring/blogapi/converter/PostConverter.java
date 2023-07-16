package com.javaspring.blogapi.converter;

import com.javaspring.blogapi.dto.PostDTO;
import com.javaspring.blogapi.model.PostEntity;
import com.javaspring.blogapi.repository.CategoryRepository;
import com.javaspring.blogapi.service.impl.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PostConverter {
    @Autowired
    private CategoryService categoryService;

    public PostEntity ConverterPostDTOToPost(PostDTO postDTO) {
        PostEntity postEntity = new PostEntity();

        BeanUtils.copyProperties(postDTO, postEntity);
        return postEntity;
    }

    public PostDTO ConverterPostToPostDTO(PostEntity postEntity) {
        PostDTO postDTO = new PostDTO();

        BeanUtils.copyProperties(postEntity, postDTO);
        postDTO.setCategoryCode(categoryService.findByCode(postEntity.getCategoryEntity().getCode()).getCode());
        postDTO.setTotalComments(postEntity.getCommentEntityList().stream().count());
        return postDTO;
    }

    public PostEntity ConverterNewPostDTOToOldPost(PostDTO newPostDTO, PostEntity oldPostEntity) {
        String createBy = oldPostEntity.getCreatedBy();
        Date createDate = oldPostEntity.getCreatedDate();
        BeanUtils.copyProperties(newPostDTO, oldPostEntity);
        oldPostEntity.setCreatedBy(createBy);
        oldPostEntity.setCreatedDate(createDate);
        return oldPostEntity;
    }
}
