package com.javaspring.blogapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_categories")
@Table(name = "tbl_categories")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryEntity extends BaseModel {
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String code;
    @OneToMany(mappedBy = "categoryEntity")
    private List<PostEntity> postEntities = new ArrayList<>();
}
