package com.javaspring.blogapi.service;

import com.javaspring.blogapi.dto.CategoryDTO;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public interface CategoryInterface {
    CategoryDTO save(CategoryDTO categoryDTO);

    List<CategoryDTO> findAllCat();

    CategoryDTO findByCode(String code);

    List<CategoryDTO> deleteCat(String code) throws DataIntegrityViolationException;
}
