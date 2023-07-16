package com.javaspring.blogapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "tbl_comment")
@Table(name = "tbl_comment")
@AllArgsConstructor
@NoArgsConstructor
@Data
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
}
