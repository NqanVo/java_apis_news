package com.javaspring.blogapi.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity(name = "tbl_posts")
@Table(name = "tbl_posts")
public class PostEntity extends BaseModel {
    @Column
    private String title;
    @Column
    private String thumbnail;
    @Column
    private String shortDesc;
    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "id_category") //ten cot khoa ngoai id_category trong bang post
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL)
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    public UserEntity getUser() {
        return userEntity;
    }

    public void setUser(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CategoryEntity getCategory() {
        return categoryEntity;
    }

    public void setCategory(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public List<CommentEntity> getCommentEntityList() {
        return commentEntityList;
    }

    public void setCommentEntityList(List<CommentEntity> commentEntityList) {
        this.commentEntityList = commentEntityList;
    }
}
