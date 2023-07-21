package com.javaspring.blogapi.converter;

import com.javaspring.blogapi.dto.category.CategoryRequestDTO;
import com.javaspring.blogapi.dto.category.CategoryResponseDTO;
import com.javaspring.blogapi.model.CategoryEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Date;
import java.util.regex.Pattern;

@Component
public class CategoryConverter {
    public CategoryResponseDTO EntityToDTO(CategoryEntity categoryEntity) {
        CategoryResponseDTO temp = new CategoryResponseDTO();
        BeanUtils.copyProperties(categoryEntity, temp);
        return temp;
    }

    public CategoryEntity DTOToEntity(CategoryRequestDTO categoryRequestDTO) {
        CategoryEntity temp = new CategoryEntity();
//        String code = NameToCode(categoryDTO.getName());
//        temp.setName(categoryDTO.getName());
        BeanUtils.copyProperties(categoryRequestDTO, temp);
        temp.setCode(NameToCode(categoryRequestDTO.getName()));

        //converter date and by
        return temp;
    }

    public CategoryEntity UpdateInfo_DTOToEntity(CategoryRequestDTO categoryRequestDTO, CategoryEntity categoryEntity){
        String createBy = categoryEntity.getCreatedBy();
        Date createDate = categoryEntity.getCreatedDate();

        BeanUtils.copyProperties(categoryRequestDTO, categoryEntity);
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
