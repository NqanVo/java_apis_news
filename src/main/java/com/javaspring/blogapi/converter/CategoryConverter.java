package com.javaspring.blogapi.converter;

import com.javaspring.blogapi.dto.CategoryDTO;
import com.javaspring.blogapi.dto.user.UserUpdateDTO;
import com.javaspring.blogapi.model.CategoryEntity;
import com.javaspring.blogapi.model.UserEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Date;
import java.util.regex.Pattern;

@Component
public class CategoryConverter {
    public CategoryDTO EntityToDTO(CategoryEntity categoryEntity) {
        CategoryDTO temp = new CategoryDTO();
//        temp.setName(categoryEntity.getName());
//        temp.setCode(categoryEntity.getCode());
        BeanUtils.copyProperties(categoryEntity, temp);
        //converter date and by
        return temp;
    }

    public CategoryEntity DTOToEntity(CategoryDTO categoryDTO) {
        CategoryEntity temp = new CategoryEntity();
//        String code = NameToCode(categoryDTO.getName());
//        temp.setName(categoryDTO.getName());
        BeanUtils.copyProperties(categoryDTO, temp);
        temp.setCode(NameToCode(categoryDTO.getName()));

        //converter date and by
        return temp;
    }

    public CategoryEntity UpdateInfo_DTOToEntity(CategoryDTO categoryDTO, CategoryEntity categoryEntity){
        String createBy = categoryEntity.getCreatedBy();
        Date createDate = categoryEntity.getCreatedDate();

        BeanUtils.copyProperties(categoryDTO, categoryEntity);
        categoryEntity.setCreatedBy(createBy);
        categoryEntity.setCreatedDate(createDate);

        return categoryEntity;
    }

    public String NameToCode(String value) {
        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
