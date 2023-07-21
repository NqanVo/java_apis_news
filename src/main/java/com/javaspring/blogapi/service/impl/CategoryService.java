package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.converter.CategoryConverter;
import com.javaspring.blogapi.dto.category.CategoryRequestDTO;
import com.javaspring.blogapi.dto.category.CategoryResponseDTO;
import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.model.CategoryEntity;
import com.javaspring.blogapi.repository.CategoryRepository;
import com.javaspring.blogapi.service.CategoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService implements CategoryInterface {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryConverter categoryConverter;

    @Override
    public CategoryResponseDTO save(CategoryRequestDTO categoryRequestDTO) {
        CategoryEntity temp;
        categoryRequestDTO.setCode(categoryConverter.NameToCode(categoryRequestDTO.getName()));
        if (categoryRequestDTO.getId() == null) {
            if (categoryRepository.existsByCode(categoryRequestDTO.getCode()))
                throw new CustomException.BadRequestException("Danh mục đã tồn tại: " + categoryRequestDTO.getCode());
            temp = categoryConverter.DTOToEntity(categoryRequestDTO);
        } else {
            // * Lấy danh mục theo id
            temp = categoryRepository.findById(categoryRequestDTO.getId()).orElseThrow(() -> new CustomException.NotFoundException("Không tìm thấy danh mục cần sửa: " + categoryRequestDTO.getId()));
            // * Lấy danh mục theo code mới
            CategoryEntity temp2 = categoryRepository.findByCode(categoryRequestDTO.getCode());
            // * Nếu code mới đã tồn tại và temp.id != temp2.id => danh mục trùng nhau
            if (temp2 != null)
                if (!(temp.getId().equals(temp2.getId())))
                    throw new CustomException.BadRequestException("Danh mục đã tồn tại: " + temp2.getCode());
            temp = categoryConverter.UpdateInfo_DTOToEntity(categoryRequestDTO, temp);
        }
        temp = categoryRepository.save(temp);
        return categoryConverter.EntityToDTO(temp);
    }

    @Override
    public List<CategoryResponseDTO> findAllCat() {
        List<CategoryResponseDTO> listDto = new ArrayList<>();
        List<CategoryEntity> listEntity = categoryRepository.findAll();
        for (CategoryEntity cat : listEntity)
            listDto.add(categoryConverter.EntityToDTO(cat));
        return listDto;
    }

    @Override
    public CategoryResponseDTO findByCode(String code) {
        CategoryEntity entity = categoryRepository.findByCode(code);
        if (entity == null) throw new CustomException.NotFoundException("Khong tìm thấy danh mục: " + code);
        return categoryConverter.EntityToDTO(entity);
    }

    @Override
    public List<CategoryResponseDTO> deleteCat(String code) throws DataIntegrityViolationException {
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
