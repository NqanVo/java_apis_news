package com.javaspring.blogapi.service;

import com.javaspring.blogapi.dto.category.CategoryRequestDTO;
import com.javaspring.blogapi.dto.category.CategoryResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public interface CategoryInterface {
    CategoryResponseDTO save(CategoryRequestDTO categoryRequestDTO);

    List<CategoryResponseDTO> findAllCat();

    CategoryResponseDTO findByCode(String code);

    List<CategoryResponseDTO> deleteCat(String code) throws DataIntegrityViolationException;
}
