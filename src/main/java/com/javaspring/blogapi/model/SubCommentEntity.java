package com.javaspring.blogapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "tbl_sub_comment")
@Table(name = "tbl_sub_comment")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubCommentEntity extends BaseModel{
    @Column(nullable = false)
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comment")
    private CommentEntity commentEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserEntity userEntity;

}
