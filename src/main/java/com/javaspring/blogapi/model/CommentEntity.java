package com.javaspring.blogapi.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_comment")
@Table(name = "tbl_comment")
public class CommentEntity extends BaseModel{
    @Column(nullable = false)
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_post")
    private PostEntity postEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "commentEntity",cascade = CascadeType.ALL)
    private List<SubCommentEntity> subCommentEntityList = new ArrayList<>();


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PostEntity getPostEntity() {
        return postEntity;
    }

    public void setPostEntity(PostEntity postEntity) {
        this.postEntity = postEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public List<SubCommentEntity> getSubCommentEntityList() {
        return subCommentEntityList;
    }

    public void setSubCommentEntityList(List<SubCommentEntity> subCommentEntityList) {
        this.subCommentEntityList = subCommentEntityList;
    }
}
