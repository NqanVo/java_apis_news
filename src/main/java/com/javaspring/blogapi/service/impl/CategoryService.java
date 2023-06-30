package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.converter.CategoryConverter;
import com.javaspring.blogapi.dto.CategoryDTO;
import com.javaspring.blogapi.dto.error.ErrorDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.CategoryEntity;
import com.javaspring.blogapi.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryConverter categoryConverter;

    public CategoryDTO save(CategoryDTO categoryDTO) {
        CategoryEntity temp;
        categoryDTO.setCode(categoryConverter.NameToCode(categoryDTO.getName()));
        if (categoryDTO.getId() == null) {
            if (categoryRepository.existsByCode(categoryDTO.getCode()))
                throw new CustomException.BadRequestException("Danh mục đã tồn tại: " + categoryDTO.getCode());
            temp = categoryConverter.DTOToEntity(categoryDTO);
        } else {
            // * Lấy danh mục theo id
            temp = categoryRepository.findById(categoryDTO.getId()).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy danh mục cần sửa: " + categoryDTO.getId()));
            // * Lấy danh mục theo code mới
            CategoryEntity temp2 = categoryRepository.findByCode(categoryDTO.getCode());
            // * Nếu code mới đã tồn tại và temp.id != temp2.id => danh mục trùng nhau
            if (temp2 != null)
                if (!(temp.getId().equals(temp2.getId())))
                    throw new CustomException.BadRequestException("Danh mục đã tồn tại: " + temp2.getCode());
            temp = categoryConverter.UpdateInfo_DTOToEntity(categoryDTO, temp);
        }
        temp = categoryRepository.save(temp);
        return categoryConverter.EntityToDTO(temp);
    }

    public List<CategoryDTO> findAllCat() {
        List<CategoryDTO> listDto = new ArrayList<>();
        List<CategoryEntity> listEntity = categoryRepository.findAll();
        for (CategoryEntity cat : listEntity)
            listDto.add(categoryConverter.EntityToDTO(cat));
        return listDto;
    }

    public CategoryDTO findByCode(String code) {
        CategoryEntity entity = categoryRepository.findByCode(code);
        if (entity == null) throw new CustomException.NotFoundException("Khong tìm thấy danh mục: " + code);
        return categoryConverter.EntityToDTO(entity);
    }

    public List<CategoryDTO> deleteCat(String code) throws DataIntegrityViolationException {
        try {
            if (!categoryRepository.existsByCode(code))
                throw new CustomException.NotFoundException("Không tìm thấy danh mục: " + code);
            categoryRepository.deleteByCode(code);
            return findAllCat();
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException.BadRequestException("Không thể xóa danh mục khi còn bài viết bên trong");
        }
    }
}
