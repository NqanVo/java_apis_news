package com.javaspring.blogapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity(name = "tbl_posts")
@Table(name = "tbl_posts")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostEntity extends BaseModel {
    @Column
    private String title;
    @Column
    private String thumbnail;
    @Column
    private String shortDesc;
//    @Column(columnDefinition = "LONGTEXT") //MySQL
    @Column(columnDefinition = "TEXT") //PostgreSQL 
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "id_category") //ten cot khoa ngoai id_category trong bang post
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL)
    private List<CommentEntity> commentEntityList = new ArrayList<>();
}
