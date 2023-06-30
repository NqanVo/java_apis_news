package com.javaspring.blogapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_categories")
@Table(name = "tbl_categories")
public class CategoryEntity extends BaseModel {
    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @OneToMany(mappedBy = "categoryEntity")
    private List<PostEntity> postEntities = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<PostEntity> getPosts() {
        return postEntities;
    }

    public void setPosts(List<PostEntity> postEntities) {
        this.postEntities = postEntities;
    }
}
