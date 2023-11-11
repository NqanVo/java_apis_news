package com.javaspring.blogapi.repository;

import com.javaspring.blogapi.model.CategoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    CategoryEntity findByCode(String code);

    boolean existsByCode(String code);

    @Transactional
    @Modifying
    @Query("delete from tbl_categories c where c.code = ?1")
    void deleteByCode(String code);

}
